import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCount {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // Текст для обробки
        String text = "This is a sample text. This text is for word count. Word count is done by concurrent tasks.";
        String[] words = text.split("\\s+"); // Розбиваємо текст на слова

        // ConcurrentHashMap для зберігання частоти слів
        ConcurrentHashMap<String, AtomicInteger> wordCountMap = new ConcurrentHashMap<>();

        // Розбиваємо текст на частини
        List<String[]> parts = splitArray(words, 3); // Розбиваємо на 3 частини

        // Використовуємо ExecutorService для паралельного виконання завдань
        ExecutorService executorService = Executors.newFixedThreadPool(parts.size());

        // Список для зберігання Future результатів
        List<Future<Void>> futures = new ArrayList<>();

        // Створюємо Callable для кожної частини тексту
        for (String[] part : parts) {
            Callable<Void> task = () -> {
                for (String word : part) {
                    wordCountMap.computeIfAbsent(word.toLowerCase(), k -> new AtomicInteger(0)).incrementAndGet();
                }
                return null;
            };
            futures.add(executorService.submit(task));
        }

        // Чекаємо на завершення всіх завдань
        for (Future<Void> future : futures) {
            while (!future.isDone()) {
                System.out.println("Задача ще виконується...");
                Thread.sleep(100); // Перевіряємо кожні 100 мілісекунд
            }
        }

        // Виводимо результат
        System.out.println("Підрахунок слів завершено:");
        wordCountMap.forEach((word, count) -> System.out.println(word + ": " + count));

        // Завершуємо роботу ExecutorService
        executorService.shutdown();
    }

    // Метод для розбиття масиву слів на кілька частин
    private static List<String[]> splitArray(String[] array, int parts) {
        List<String[]> result = new ArrayList<>();
        int length = array.length;
        int chunkSize = (length + parts - 1) / parts;

        for (int i = 0; i < length; i += chunkSize) {
            result.add(Arrays.copyOfRange(array, i, Math.min(length, i + chunkSize)));
        }

        return result;
    }
}

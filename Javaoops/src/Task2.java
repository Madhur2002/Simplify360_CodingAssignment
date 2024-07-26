import java.util.*;

class Task2 {
    static class Pair<K, V> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    Map<String, Set<String>> graph;

    public Task2() {
        graph = new HashMap<>();
    }

    public void addFriend(String person1, String person2) {
        graph.computeIfAbsent(person1, k -> new HashSet<>()).add(person2);
        graph.computeIfAbsent(person2, k -> new HashSet<>()).add(person1);
    }

    public Set<String> commonFriends(String person1, String person2) {
        Set<String> commonFriends = new HashSet<>();
        for (String friend : graph.getOrDefault(person1, Collections.emptySet())) {
            if (graph.getOrDefault(person2, Collections.emptySet()).contains(friend)) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }

    public int connectionLevel(String start, String end) {
        Queue<Pair<String, Integer>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(new Pair<>(start, 0));
        visited.add(start);

        while (!queue.isEmpty()) {
            Pair<String, Integer> pair = queue.poll();
            String person = pair.getKey();
            int level = pair.getValue();

            if (person.equals(end)) {
                return level;
            }

            for (String neighbor : graph.getOrDefault(person, Collections.emptySet())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(new Pair<>(neighbor, level + 1));
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task2 graph = new Task2();

        // Collect number of friendships
        System.out.println("Enter number of friendships to add:");
        int numFriends = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Collect all friendships
        System.out.println("Enter friendships (format: person1 person2):");
        for (int i = 0; i < numFriends; i++) {
            String[] input = scanner.nextLine().split(" ");
            if (input.length == 2) {
                String person1 = input[0];
                String person2 = input[1];
                graph.addFriend(person1, person2);
            }
        }

        // Collect common friends queries
        System.out.println("Enter number of queries for common friends:");
        int numCommonFriendsQueries = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter queries for common friends (format: person1 person2):");
        for (int i = 0; i < numCommonFriendsQueries; i++) {
            String[] query = scanner.nextLine().split(" ");
            if (query.length == 2) {
                String person1 = query[0];
                String person2 = query[1];
                System.out.println("Common friends of " + person1 + " and " + person2 + ": " + graph.commonFriends(person1, person2));
            }
        }

        // Collect connection level queries
        System.out.println("Enter number of queries for connection levels:");
        int numConnectionLevelQueries = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter queries for connection levels (format: start end):");
        for (int i = 0; i < numConnectionLevelQueries; i++) {
            String[] query = scanner.nextLine().split(" ");
            if (query.length == 2) {
                String start = query[0];
                String end = query[1];
                System.out.println("Connection level between " + start + " and " + end + ": " + graph.connectionLevel(start, end));
            }
        }

        // Output friends of Alice and Bob
        System.out.println("Friends of Alice: " + graph.graph.get("Alice"));
        System.out.println("Friends of Bob: " + graph.graph.get("Bob"));

        scanner.close();
    }
}

//Time Complexity : O(N+M) where N is the number of people and M is the number of friendships.
//Space Complexity : O(N)


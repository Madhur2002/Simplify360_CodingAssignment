import java.util.*;

class Task {
    String id;
    int duration;
    int est, eft, lst, lft;
    List<Task> dependencies = new ArrayList<>();

    Task(String id, int duration) {
        this.id = id;
        this.duration = duration;
    }
}

class Workflow {
    private Map<String, Task> tasks = new HashMap<>();
    private Map<String, List<String>> graph = new HashMap<>();
    private Map<String, Integer> inDegree = new HashMap<>();

    public void addTask(String id, int duration) {
        if (!tasks.containsKey(id)) {
            tasks.put(id, new Task(id, duration));
            graph.put(id, new ArrayList<>());
            inDegree.put(id, 0);
        }
    }

    public void addDependency(String fromId, String toId) {
        if (!tasks.containsKey(fromId) || !tasks.containsKey(toId)) {
            throw new IllegalArgumentException("Task not found");
        }
        tasks.get(fromId).dependencies.add(tasks.get(toId));
        graph.get(fromId).add(toId);
        inDegree.put(toId, inDegree.get(toId) + 1);
    }

    public void calculateTimes() {
        calculateEFTs();
        calculateLFTs();
    }

    private void calculateEFTs() {
        Queue<String> queue = new LinkedList<>();
        for (String taskId : tasks.keySet()) {
            if (inDegree.get(taskId) == 0) {
                Task task = tasks.get(taskId);
                task.est = 0;
                task.eft = task.duration;
                queue.add(taskId);
            }
        }

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            Task currentTask = tasks.get(currentId);

            for (String neighborId : graph.get(currentId)) {
                Task neighbor = tasks.get(neighborId);
                neighbor.est = Math.max(neighbor.est, currentTask.eft);
                neighbor.eft = neighbor.est + neighbor.duration;
                inDegree.put(neighborId, inDegree.get(neighborId) - 1);

                if (inDegree.get(neighborId) == 0) {
                    queue.add(neighborId);
                }
            }
        }
    }

    private void calculateLFTs() {
        int projectFinishTime = tasks.values().stream()
                .mapToInt(task -> task.eft)
                .max()
                .orElse(0);

        for (Task task : tasks.values()) {
            task.lft = projectFinishTime;
            task.lst = task.lft - task.duration;
        }

        Queue<String> queue = new LinkedList<>();
        for (String taskId : tasks.keySet()) {
            if (graph.get(taskId).isEmpty()) {
                queue.add(taskId);
            }
        }

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            Task currentTask = tasks.get(currentId);

            for (String taskId : tasks.keySet()) {
                if (graph.get(taskId).contains(currentId)) {
                    Task predecessor = tasks.get(taskId);
                    predecessor.lft = Math.min(predecessor.lft, currentTask.lst);
                    predecessor.lst = predecessor.lft - predecessor.duration;

                    // Update in-degree and add to queue if it's now 0
                    int updatedInDegree = inDegree.get(taskId) - 1;
                    inDegree.put(taskId, updatedInDegree);
                    if (updatedInDegree == 0) {
                        queue.add(taskId);
                    }
                }
            }
        }
    }

    public int getEarliestCompletionTime() {
        return tasks.values().stream()
                .mapToInt(task -> task.eft)
                .max()
                .orElse(0);
    }

    public int getLatestCompletionTime() {
        return tasks.values().stream()
                .mapToInt(task -> task.lft)
                .min()
                .orElse(0);
    }
}

public class Task1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Workflow workflow = new Workflow();

        try {
            // Collect task data from the user
            System.out.println("Enter the number of tasks:");
            int numTasks = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.println("Enter tasks (format: id duration):");
            for (int i = 0; i < numTasks; i++) {
                String[] input = scanner.nextLine().split(" ");
                if (input.length == 2) {
                    String id = input[0];
                    int duration = Integer.parseInt(input[1]);
                    workflow.addTask(id, duration);
                } else {
                    System.out.println("Invalid task input. Expected format: id duration");
                }
            }

            // Collect dependency data from the user
            System.out.println("Enter the number of dependencies:");
            int numDependencies = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.println("Enter dependencies (format: fromId toId):");
            for (int i = 0; i < numDependencies; i++) {
                String[] input = scanner.nextLine().split(" ");
                if (input.length == 2) {
                    String fromId = input[0];
                    String toId = input[1];
                    workflow.addDependency(fromId, toId);
                } else {
                    System.out.println("Invalid dependency input. Expected format: fromId toId");
                }
            }

            // Calculate times and display results
            workflow.calculateTimes();

            System.out.println("Earliest time all tasks will be completed: " + workflow.getEarliestCompletionTime());
            System.out.println("Latest time all tasks will be completed: " + workflow.getLatestCompletionTime());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input type. Please enter integers where expected.");
        } finally {
            scanner.close();
        }
    }
}


//Time Complexity : O(V+E) where V is the number of vertices and E is the number of edges.
//Space Complexity : O(N)

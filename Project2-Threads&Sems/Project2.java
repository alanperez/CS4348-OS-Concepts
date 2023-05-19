import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.Queue;
import java.util.Random;

public class Project2 {
//	ints
    public static final int customer_max = 50;
    public static final int max_customers_inside_office = 10;
    public static final int postal_worker_max = 3;
    public static int completed = 0;
//    Semaphores, went through trial and error some should not be here
    public static Semaphore completedCustomersMutex = new Semaphore(1, true);
    public static Semaphore scales_access = new Semaphore(1, true);
    public static Semaphore post_office_capacity = new Semaphore(max_customers_inside_office, true);
    public static Semaphore customer_info = new Semaphore(1, true);
    public static Semaphore customer_request = new Semaphore(0, true);
    public static Semaphore get_customer_info = new Semaphore(0, true);
    public static Semaphore worker_handle_request = new Semaphore(1, true);
    public static Semaphore finished = new Semaphore(0, true);
    public static Semaphore customer_waiting = new Semaphore(0, true);
    public static Semaphore postal_worker_ready = new Semaphore(0, true);
    public static Semaphore available_postal_worker = new Semaphore(3, true);
    public static Semaphore postal_worker_mutex = new Semaphore(1, true);
    public static Semaphore queueAccess = new Semaphore(1, true);

//    delay
    public static final int STAMPS_DELAY = 60;
    public static final int LETTER_DELAY = 90;
    public static final int PACKAGE_DELAY = 120;

//    ds to store ids
    public static Queue<Integer> customer_queue = new ArrayDeque<>();
    public static Queue<Customer.Task> customer_tasks = new ArrayDeque<>();
    public static Queue<Integer> queue = new LinkedList<>();
    public static Map<Thread, Integer> threadToCustomerIdMap = new ConcurrentHashMap<>();
    public static Map<Integer, Semaphore> finishedSemaphores = new ConcurrentHashMap<>();
    
    public static class Customer implements Runnable {
        public int id;
        public Task task;
        public int worker_id;

        enum Task {
            BUY_STAMPS,
            MAIL_LETTER,
            MAIL_PACKAGE;
        }

        public Customer(int id) {
            Random rand = new Random();
            this.id = id;
            this.task = Task.values()[rand.nextInt(3)];
        }

        @Override
        public void run() {
            try {
                threadToCustomerIdMap.put(Thread.currentThread(), id);
                System.out.println("Customer " + id + " created");
                post_office_capacity.acquire();

                System.out.println("Customer " + id + " enters post office");
                customer_waiting.release();
                postal_worker_ready.acquire();
                customer_info.acquire();

                queueAccess.acquire();
                queue.add(id);
                customer_tasks.add(task);
                queueAccess.release();

                customer_request.acquire();
                get_customer_info.acquire();
                worker_id = threadToCustomerIdMap.get(Thread.currentThread());
                System.out.println("Customer " + id + " asks postal worker " + worker_id + " to " + task);

                finishedSemaphores.get(worker_id).acquire();
                System.out.println("Customer " + id + " finishes with postal worker " + worker_id);
                post_office_capacity.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class PostalWorker implements Runnable {
        public int id;

        public PostalWorker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
        	 try {
                 System.out.println("Postal worker " + id + " created.");
                 while (true) {
                     available_postal_worker.acquire();
                     customer_waiting.acquire();
                     postal_worker_ready.release();
//	trying to ensure one thread can modify a var at a time
                     customer_info.acquire();
                     queueAccess.acquire();
                     Integer customer_id = queue.poll();
                     Customer.Task customer_task = customer_tasks.poll();
                     queueAccess.release();
                     if (customer_id == null) {
                         completedCustomersMutex.acquire();
                         if (completed >= customer_max) {
                             completedCustomersMutex.release();
                             break;
                         }
                         completedCustomersMutex.release();
                         continue;
                     }
                     System.out.println("Postal worker " + id + " serving customer " + customer_id);
                     customer_request.acquire();

                     threadToCustomerIdMap.put(Thread.currentThread(), id); // Store the worker id
                     get_customer_info.release();

                     switch (customer_task) {
                         case BUY_STAMPS:
                             Thread.sleep(STAMPS_DELAY);
                             break;
                         case MAIL_LETTER:
                             Thread.sleep(LETTER_DELAY);
                             break;
                         case MAIL_PACKAGE:
                             scales_access.acquire();
                             System.out.println("Scales in use by postal worker " + id);
                             Thread.sleep(PACKAGE_DELAY);
                             System.out.println("Scales released by postal worker " + id);
                             scales_access.release();
                             break;
                     }
                     System.out.println("Postal worker " + id + " finished serving customer " + customer_id);
                     finishedSemaphores.get(id).release();
                     completedCustomersMutex.acquire();
                     completed++;
                     completedCustomersMutex.release();
                 }
             } 
        	 
        	 
        	 catch (InterruptedException e) {
                 e.printStackTrace();
             }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Simulating Post Office with " + customer_max + " customers and " + postal_worker_max + " postal workers");

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < postal_worker_max; i++) {
            finishedSemaphores.put(i, new Semaphore(0, true));
            PostalWorker postalWorker = new PostalWorker(i);
            Thread thread = new Thread(postalWorker);
            thread.start();
            threads.add(thread);
        }

        for (int i = 0; i < customer_max; i++) {
            Customer customer = new Customer(i);
            Thread thread = new Thread(customer);
            thread.start();
            threads.add(thread);
            Thread.sleep(100);
        }

        for (Thread thread : threads) {
            thread.join();
            System.out.println("Joined customer " + thread);
        }
    }
}

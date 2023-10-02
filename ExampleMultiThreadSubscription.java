import com.thomsonreuters.ema.access.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSymbolSubscriber {
    public static void main(String[] args) {
        OmmConsumer consumer = null;

        try {
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig();

            // Configure the EMA consumer
            config.host("hostname"); // Replace with your hostname
            config.username("username"); // Replace with your username
            config.password("password"); // Replace with your password

            consumer = EmaFactory.createOmmConsumer(config);

            // Read symbols from a file
            BufferedReader reader = new BufferedReader(new FileReader("symbol_list.txt"));
            List<String> symbols = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                symbols.add(line);
            }
            reader.close();

            // Create a thread pool for parallel subscriptions
            int numThreads = 10; // Adjust the number of threads as needed
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            // Submit subscription tasks to the thread pool
            for (String symbol : symbols) {
                Runnable task = new SubscriptionTask(consumer, symbol);
                executor.submit(task);
            }

            // Shutdown the thread pool when all tasks are completed
            executor.shutdown();

        } catch (OmmException | IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
        } finally {
            if (consumer != null) {
                consumer.uninitialize();
            }
        }
    }
}

class SubscriptionTask implements Runnable {
    private OmmConsumer consumer;
    private String symbol;

    public SubscriptionTask(OmmConsumer consumer, String symbol) {
        this.consumer = consumer;
        this.symbol = symbol;
    }

    @Override
    public void run() {
        try {
            ReqMsg reqMsg = EmaFactory.createReqMsg();
            reqMsg.payloadType(DataType.Msg);
            reqMsg.name(symbol);

            // Register interest in the symbol
            long handle = consumer.registerClient(reqMsg, new MarketDataClient());

            // Simulate some processing time for the subscription
            Thread.sleep(1000); // Adjust as needed

            // Unregister the interest
            consumer.unregister(handle);
        } catch (OmmException | InterruptedException ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }
}

class MarketDataClient implements OmmConsumerClient {
    @Override
    public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
        // Handle the received data here
        System.out.println("Received RefreshMsg: " + refreshMsg.toString());
    }

    @Override
    public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {
        // Handle the received data updates here
        System.out.println("Received UpdateMsg: " + updateMsg.toString());
    }

    @Override
    public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent event) {
        // Handle status messages here
        System.out.println("Received StatusMsg: " + statusMsg.toString());
    }
}

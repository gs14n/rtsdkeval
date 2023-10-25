import com.thomsonreuters.ema.access.*;

public class EMAContributor {
    public static void main(String[] args) {
        OmmProvider provider = EmaFactory.createOmmProvider(EmaFactory.createOmmIProviderConfig());
        
        // Create a MarketPrice update message
        UpdateMsg updateMsg = EmaFactory.createUpdateMsg();
        
        // Set the message's RIC (instrument name) and update service name
        updateMsg.payload(EmaFactory.createFieldList().addReal(22, 135.60, OmmReal.MagnitudeType.EXPONENT_NEG_2));
        updateMsg.serviceName("ELEKTRON_EDGE");  // Replace with the appropriate service name
        updateMsg.name("AAPL.O");  // Replace with the desired instrument name
        
        // Continuously send the update message
        while (true) {
            // Submit the update message
            provider.submit(updateMsg, null);
            try {
                Thread.sleep(1000);  // Send updates every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

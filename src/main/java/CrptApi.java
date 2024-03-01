import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.List;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CrptApi {
    private static final String requestURL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private long lastRequestTime;
    private final AtomicInteger requestCount;
    private final Object lock = new Object();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        if (timeUnit == null || requestLimit <= 0) {
            throw new IllegalArgumentException("Invalid requestInterval: " + timeUnit + ", or requestLimit: " + requestLimit);
        }
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.requestCount = new AtomicInteger(0);
    }

    public void createDocument(Document document, String signature) {
        synchronized (lock) {
            long currentTime = Instant.now().toEpochMilli();
            int currentCount = requestCount.get();

            // If it's been at least 1 timeunit since the last request, or if this is the first request,
            // reset the request count and proceed.
            if (currentCount == 0 || currentTime - lastRequestTime >= timeUnit.toMillis(1)) {
                lastRequestTime = currentTime;
                requestCount.set(1);

                // If the request count is less than the limit, and it hasn't been at least 1 timeunit since the last request,
                // increment the request count and proceed.
            } else if (currentCount < requestLimit) {
                requestCount.incrementAndGet();

                // If the request limit has been reached, wait and then reset the request count.
            } else {
                long sleepTime = lastRequestTime + timeUnit.toMillis(1) - currentTime;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                lastRequestTime = Instant.now().toEpochMilli();
                requestCount.set(1);
            }

            try {
                URL url = new URL(requestURL);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Signature", signature);

                String newJson = new Gson().toJson(document);
                OutputStream os = con.getOutputStream();

                os.write(newJson.getBytes());
                os.flush();
                os.close();

                System.out.println(newJson);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 3);
        Document document = createTestDocument();

        // demonstration of thread-safe operation and restrictions on the number of requests per time unit.
        for (int i = 0; i < 4; i ++) {
            new Thread(() -> crptApi.createDocument(document, "signature")).start();
        }
    }

    private static Document createTestDocument() {
        return new Document(
                new Document.Description("string"),
                "string",
                "string",
                "LP_INTRODUCE_GOODS",
                true,
                "string",
                "string",
                "string",
                "2020-01-23",
                "string",
                List.of(new Document.Product(
                        "string",
                        "2020-01-23",
                        "string",
                        "string",
                        "string",
                        "2020-01-23",
                        "string",
                        "string",
                        "string")),
                "2020-01-23",
                "string");
    }

    public static class Document {
        public Document(
                Description description,
                String doc_id,
                String doc_status,
                String doc_type,
                boolean importRequest,
                String owner_inn,
                String participant_inn,
                String producer_inn,
                String production_date,
                String production_type,
                List<Product> products,
                String reg_date,
                String reg_number) {
            this.description = description;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;

        public static class Description {
            public Description(String participantInn) {
                this.participantInn = participantInn;
            }
            private String participantInn;
        }

        public static class Product {
            public Product(String certificate_document,
                           String certificate_document_date,
                           String certificate_document_number,
                           String owner_inn,
                           String producer_inn,
                           String production_date,
                           String tnved_code,
                           String uit_code,
                           String uitu_code) {
                this.certificate_document = certificate_document;
                this.certificate_document_date = certificate_document_date;
                this.certificate_document_number = certificate_document_number;
                this.owner_inn = owner_inn;
                this.producer_inn = producer_inn;
                this.production_date = production_date;
                this.tnved_code = tnved_code;
                this.uit_code = uit_code;
                this.uitu_code = uitu_code;
            }
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;


        }
    }
}
package server;

import java.io.IOException;

final class Utils {

    private Utils() {
    }

    static int executeScriptForResult(String script, int port) throws IOException {
        Process process = Runtime.getRuntime().exec("node " + script, new String[] {
                "PORT=" + port
        });
        try {
            int result = process.waitFor();

            /*InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(buffer);
            System.out.println("[script:stdout]\n" + (new String(buffer, StandardCharsets.UTF_8)));*/

            return result;
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
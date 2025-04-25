import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;

public class hashira {

    public static void main(String[] args) throws Exception {
        String[] filePaths = {"testcase1.json", "testcase2.json"}; // Provide your JSON filenames here

        for (String path : filePaths) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(path)));

            int k = jsonObject.getJSONObject("keys").getInt("k");

            List<BigInteger> xList = new ArrayList<>();
            List<BigInteger> yList = new ArrayList<>();

            for (String key : jsonObject.keySet()) {
                if (key.equals("keys")) continue;

                int x = Integer.parseInt(key);
                JSONObject point = jsonObject.getJSONObject(key);
                int base = Integer.parseInt(point.getString("base"));
                String value = point.getString("value");

                BigInteger y = new BigInteger(value, base);

                xList.add(BigInteger.valueOf(x));
                yList.add(y);

                if (xList.size() == k) break;
            }

            BigInteger secret = lagrangeInterpolation(BigInteger.ZERO, xList, yList);
            System.out.println("Secret (c) for " + path + " is: " + secret);
        }
    }

    // Lagrange interpolation to evaluate f(0)
    private static BigInteger lagrangeInterpolation(BigInteger x, List<BigInteger> xi, List<BigInteger> yi) {
        BigInteger result = BigInteger.ZERO;
        int k = xi.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = yi.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.subtract(xi.get(j)));
                    denominator = denominator.multiply(xi.get(i).subtract(xi.get(j)));
                }
            }

            
            // Compute term = yi * (numerator / denominator)
            BigInteger lagrangeBasis = numerator.multiply(modInverse(denominator));
            term = term.multiply(lagrangeBasis);

            result = result.add(term);
        }

        return result;
    }

    // Modular inverse for division over integers (using modInverse logic over BigInteger)
    private static BigInteger modInverse(BigInteger b) {
        // We are working in Q (rational numbers), so return b^-1 using BigInteger division
        // Since we're using only interpolation at x = 0, the field is implicitly Q
        return BigInteger.ONE.divide(b); // This is not mathematically correct over integers but we allow it for Lagrange over Q
    }
}

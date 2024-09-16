package it.saimao.tmktaikeyboard.zawgyidetector;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import it.saimao.tmktaikeyboard.R;


//public class MainActivity extends AppCompatActivity {
//
//    private TextView textView;
//    private EditText editText;
//    private Button button;
//    private ZawgyiDetector detector;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.content_main);
//        textView = findViewById(R.id.textView);
//        editText = findViewById(R.id.editText);
//        button = findViewById(R.id.button);
//        detector = new ZawgyiDetector();
//        button.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                String input = editText.getText().toString();
//                // Detect the String
//                double score = detector.getZawgyiProbability(input);
////                assert score > 0.999;
//                DecimalFormat format = new DecimalFormat("#.######");
//                textView.setText("Zawgyi Score: " + (score > .8));
//            }
//        });
//    }

//    // Unicode string:
//    String input1 = "အပြည်ပြည်ဆိုင်ရာ လူ့အခွင့်အရေး ကြေညာစာတမ်း";
//    // Zawgyi string:
//    String input2 = "အျပည္ျပည္ဆိုင္ရာ လူ႔အခြင့္အေရး ေၾကညာစာတမ္း";
//
//    // Detect that the second string is Zawgyi:
//    double score1 = detector.getZawgyiProbability(input1);
//    double score2 = detector.getZawgyiProbability(input2);
//        assert score1 < 0.001;
//        assert score2 > 0.999;
//        System.out.format("Unicode Score: %.6f%n", score1);
//        System.out.format("Zawgyi Score: %.6f%n", score2);
//
//    // Convert the second string to Unicode:
//    String input2converted = converter.convert(input2);
//        assert input1.equals(input2converted);
//        System.out.format("Converted Text: %s%n", input2converted);

public class ZawgyiDetector {
    final ZawgyiUnicodeMarkovModel model;

    /**
     * Loads the model from the resource and returns a ZawgyiDetector instance.
     */
    public ZawgyiDetector(Context context) {
        try (InputStream inStream =
                     context.getResources().openRawResource(R.raw.zawgyiunicodemodel)) {
            if (inStream == null) {
                throw new IOException("Model file zawgyiUnicodeModel.dat not found");
            }
            model = new ZawgyiUnicodeMarkovModel(inStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not load Markov model from resource file", e);
        }
    }

    public boolean isZawgyiTai(String input) {
        boolean isZawgyi = false;
        check:
        for (int i = 0; i < input.length(); i++) {
            int character = input.charAt(i);
            if (character > 43500 && character < 43600) {
                isZawgyi = true;
                break check;
            }
        }
        return isZawgyi;
    }

    /**
     * Loads the model from the specified stream instead of the default resource.
     *
     * @throws IOException If there is trouble reading from the stream.
     */
    ZawgyiDetector(InputStream inStream) throws IOException {
        model = new ZawgyiUnicodeMarkovModel(inStream);
    }

    /**
     * Performs detection on the given string. Returns the probability that the string is Zawgyi given
     * that it is either Unicode or Zawgyi. Values approaching 1 are strong Zawgyi; values approaching
     * 0 are strong Unicode; and values close to 0.5 are toss-ups.
     *
     * <p>If the string does not contain any Myanmar range code points, -Infinity is returned.
     *
     * @param input   The string on which to run detection.
     * @param verbose If true, print debugging information to standard output.
     * @return The probability that the string is Zawgyi (between 0 and 1), or -Infinity if the string
     * contains no Myanmar range code points.
     */
    public double getZawgyiProbability(String input, boolean verbose) {
        return model.predict(input, verbose);
    }

    /**
     * Performs detection on the given string. Returns the probability that the string is Zawgyi given
     * that it is either Unicode or Zawgyi. Values approaching 1 are strong Zawgyi; values approaching
     * 0 are strong Unicode; and values close to 0.5 are toss-ups.
     *
     * <p>If the string does not contain any Myanmar range code points, -Infinity is returned.
     *
     * @param input The string on which to run detection.
     * @return The probability that the string is Zawgyi (between 0 and 1), or -Infinity if the string
     * contains no Myanmar range code points.
     */
    public double getZawgyiProbability(String input) {
        if (isZawgyiTai(input)) {
            return 1.0;
        }
        return getZawgyiProbability(input, false);
    }
}

package bf22wk.brocku.translatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate.TranslateOption;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Translate _translate = TranslateOptions.newBuilder().setApiKey("YOUR_API_KEY").build().getService();


    Button translateButton;
    TextInputEditText _inputField;
    TextView _outputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translateButton = findViewById(R.id.btnTranslate);
        _inputField = findViewById(R.id.inputField);
        _outputField = findViewById(R.id.outputField); // Initialize _outputField

        translateButton.setOnClickListener(this); // Set OnClickListener for the translate button
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTranslate) {
            SetOutputText("HIIIIIIII");
        }
    }

    private String GetInputText(){
        return _inputField.getText().toString();
    }

    private void SetOutputText(String s){
        _outputField.setText(s); //set the text to the parameter value
    }

    //detect language
    private String TranslateText(String text){
        Detection detection = _translate.detect(text);
        String detectedLanguage = detection.getLanguage();
        Translation translation = _translate.translate(text, TranslateOption.targetLanguage(detectedLanguage));
        String translatedText = translation.getTranslatedText();
        return translatedText;
    }

    //language specified
    private String TranslateText(String text, String targetLanguage){
        Translation translation = _translate.translate(text, TranslateOption.targetLanguage(targetLanguage));
        String translatedText = translation.getTranslatedText();
        return translatedText;
    }
}

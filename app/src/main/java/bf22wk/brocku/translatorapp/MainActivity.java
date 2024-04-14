package bf22wk.brocku.translatorapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate.TranslateOption;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //this class holds a language and its corresponding language code example: (English, EN)
    class LanguageMapper{
        String _language;
        String _code;

        public LanguageMapper(String language, String code){
            this._language = language;
            this._code = code;
        }

        public String GetLanguage(){
            return _language;
        };

        public String GetCode(){
            return _code;
        }
    }

    Translate _translate = TranslateOptions.newBuilder().setApiKey("AIzaSyAmjYp_KwOm9-gLDSGKUCmJ_xLraz6te8k").build().getService();
    Button _translateButton;
    TextInputEditText _inputField; //input box
    TextView _outputField; //output box
    Spinner _fromLanguageSpinner, _toLanguageSpinner; //the language selection spinners
    TextView _fromLanguageText, _toLanguageText; //the text above each language box
    String _fromLanguageCode, _toLanguageCode; //example, EN
    ArrayList<LanguageMapper> _lM = new ArrayList<LanguageMapper>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _translateButton = findViewById(R.id.btnTranslate);
        _inputField = findViewById(R.id.inputField);
        _outputField = findViewById(R.id.outputField); // Initialize _outputField
        _fromLanguageSpinner = findViewById(R.id.fromLanguageSpinner);
        _toLanguageSpinner = findViewById(R.id.toLanguageSpinner);
        _fromLanguageText = findViewById(R.id.fromLanguageText);
        _toLanguageText = findViewById(R.id.toLanguageText);
        _translateButton.setOnClickListener(this); // Set OnClickListener for the translate button

        new LoadLanguagesTask().execute(); //load languages into spinner

        //listener for from language spinner
        _fromLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                UpdateFromLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        //listener for to language spinner
        _toLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                UpdateToLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTranslate) {
            OnTranslationButtonPressed();
        }
    }

    private void OnTranslationButtonPressed(){
        new TranslateTask().execute(GetInputText(), _toLanguageCode);
    }

    private class TranslateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String text = params[0]; //param 0
            String targetLanguage = params[1]; //param 1

            //if source language not chosen
            if (IsDetectLanguageSelected()) return TranslateText(text, targetLanguage);

            //if source language chosen
            else return TranslateText(text, _fromLanguageCode, _toLanguageCode);
        }

        @Override
        protected void onPostExecute(String result) {
            SetOutputText(result);
        }
    }

    private class LoadLanguagesTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<Language> languages = _translate.listSupportedLanguages();
            List<String> languageNames = new ArrayList<>();
            languageNames.add("Detect"); //add detect language option
            for (Language language : languages) {
                languageNames.add(language.getName());
                _lM.add(new LanguageMapper(language.getName(), language.getCode())); //add each language and its corresponding code to the mapper
            }
            return languageNames;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //populate from Language spinner with supported languages
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _fromLanguageSpinner.setAdapter(adapter);

            //populate to language spinner with supported languages (excluding "Detect")
            List<String> toLanguageNames = new ArrayList<>(result);
            toLanguageNames.remove("Detect");
            ArrayAdapter<String> toLanguageAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, toLanguageNames);
            toLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _toLanguageSpinner.setAdapter(toLanguageAdapter);
        }
    }

    private String GetInputText(){
        return _inputField.getText().toString();
    }

    private void SetOutputText(String s){
        _outputField.setText(s); //set the text to the parameter value
    }

    //translate text (auto detect input, chosen output)
    private String TranslateText(String text, String targetLanguage) {
        Detection detection = _translate.detect(text);
        String detectionLanguage = detection.getLanguage();
        Translation translation = _translate.translate(
                text,
                TranslateOption.sourceLanguage(detectionLanguage),
                TranslateOption.targetLanguage(targetLanguage)
        );
        String translatedText = translation.getTranslatedText();
        return translatedText;
    }

    //translate text (chosen input, chosen output)
    private String TranslateText(String text, String sourceLanguage, String targetLanguage) {
        Translation translation = _translate.translate(
                text,
                TranslateOption.sourceLanguage(sourceLanguage),
                TranslateOption.targetLanguage(targetLanguage)
        );
        String translatedText = translation.getTranslatedText();
        return translatedText;
    }

    private void UpdateFromLanguage(){
        String languageText = _fromLanguageSpinner.getSelectedItem().toString();
        _fromLanguageText.setText(languageText.toString());
        _fromLanguageCode = FindLanguageCode(languageText);
    }

    private void UpdateToLanguage(){
        String languageText = _toLanguageSpinner.getSelectedItem().toString();
        _toLanguageText.setText(languageText.toString());
        _toLanguageCode = FindLanguageCode(languageText);
    }

    private String FindLanguageCode(String language){
        for (LanguageMapper languageMapper : _lM) {
            //if language mapper language string matches input, return the corresponding code
            if (language == languageMapper.GetLanguage()) return languageMapper.GetCode();
        }
        return null; //if not found
    }

    private boolean IsDetectLanguageSelected(){
        //if the first element (detect language) is selected
        if (_fromLanguageSpinner.getSelectedItemId() == 0) return true;
        return false; //otherwise return false
    }
}

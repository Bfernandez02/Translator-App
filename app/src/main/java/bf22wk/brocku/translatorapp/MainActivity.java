package bf22wk.brocku.translatorapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

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

    // This is used so that code can be run after the required action occur in requested activity
    public ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                View v=findViewById(R.id.favouritesLV);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    fetchTranslations();
                    handleTranslationListElementClick();
                    selectedTranslation = null;
                }
                else if (result.getResultCode() == 1){
                    handleFavouritesClick(v);
                }
                else if (result.getResultCode() == 2){
                    handleRecentClick(v);
                }
                else if (result.getResultCode() == 3){
                    handleSettingsClick(v);
                }
            });


    private static final int SPEECH_REQUEST_CODE = 100;
    Context context;
    Translate _translate = TranslateOptions.newBuilder().setApiKey("secret").build().getService();
    ImageView _micButton, _speakerButton;
    Button _translateButton;
    TextInputEditText _inputField; //input box
    TextView _outputField; //output box
    Spinner _fromLanguageSpinner, _toLanguageSpinner; //the language selection spinners
    TextView _fromLanguageText, _toLanguageText; //the text above each language box
    String _fromLanguageCode, _toLanguageCode; //example, EN
    ArrayList<LanguageMapper> _lM = new ArrayList<LanguageMapper>();
    private TextToSpeech _tts; //the text to speech object

    public static List<translate> recentList = new ArrayList<>();

    public static List<translate> favouriteList = new ArrayList<>();

    public static Boolean recents;

    public static translate selectedTranslation;
    String fromLanguageName;
    ArrayAdapter<String> fromLanguageSpinnerAdapter;
    ArrayAdapter<String> toLanguageSpinnerAdapter;

    DataHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch all saved translations from database
        context = this;
        db = new DataHelper(context);
        fetchTranslations();

        // Initiate widgets
        setContentView(R.layout.activity_main);
        _translateButton = findViewById(R.id.btnTranslate);
        _inputField = findViewById(R.id.inputField);
        _outputField = findViewById(R.id.outputField); // Initialize _outputField
        _fromLanguageSpinner = findViewById(R.id.fromLanguageSpinner);
        _toLanguageSpinner = findViewById(R.id.toLanguageSpinner);
        _fromLanguageText = findViewById(R.id.fromLanguageText);
        _toLanguageText = findViewById(R.id.toLanguageText);
        _micButton = findViewById(R.id.btnMic);
        _speakerButton = findViewById(R.id.btnSpeaker);
        _translateButton.setOnClickListener(this); // Set OnClickListener for the translate button
        _micButton.setOnClickListener(this);
        _speakerButton.setOnClickListener(this);
        _outputField.setMovementMethod(new ScrollingMovementMethod());
        recents = false;

        new LoadLanguagesTask().execute(); //load languages into spinner

        // Text to speech function
        _tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //if initialization succeeded
                if (status == TextToSpeech.SUCCESS){
                    _tts.setLanguage(Locale.CANADA);
                }
                else{

                }
            }
        });

        //listener for from language spinner
        _fromLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // updates spinner when clicked
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
        if (v.getId() == R.id.btnTranslate) OnTranslationButtonPressed();
        if (v.getId() == R.id.btnMic) OnMicButtonPressed();
        if (v.getId() == R.id.btnSpeaker) OnSpeakerButtonPressed();
    }

    // Runs the translation function using the API
    private void OnTranslationButtonPressed(){
        new TranslateTask().execute(GetInputText(), _toLanguageCode);
    }

    // Translates using google cloud translation api
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
            SaveRecent();
        }
    }

    // Function used to populate language options based on API's supported languages
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
            fromLanguageSpinnerAdapter = adapter;

            //populate to language spinner with supported languages (excluding "Detect")
            List<String> toLanguageNames = new ArrayList<>(result);
            toLanguageNames.remove("Detect");
            ArrayAdapter<String> toLanguageAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, toLanguageNames);
            toLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toLanguageSpinnerAdapter = toLanguageAdapter;
            _toLanguageSpinner.setAdapter(toLanguageAdapter);
        }
    }

    private String GetInputText(){
        return _inputField.getText().toString();
    }


    private  String GetOutputText(){
        return _outputField.getText().toString();
    }
    private void SetOutputText(String s){
        _outputField.setText(s); //set the text to the parameter value
    }

    //translate text (auto detect input, chosen output)
    private String TranslateText(String text, String targetLanguage) {
        Detection detection = _translate.detect(text);
        String detectionLanguage = detection.getLanguage();
        fromLanguageName = detection.getLanguage().toString();
        _fromLanguageCode = FindLanguageCode(fromLanguageName);

        // Performs translation
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

    // Updates spinner and displayed from language
    private void UpdateFromLanguage(){
        String languageText = _fromLanguageSpinner.getSelectedItem().toString();
        _fromLanguageText.setText(languageText.toString());
        _fromLanguageCode = FindLanguageCode(languageText);
    }

    // Updates spinner and displayed to language
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


    // Saves translation as a recent translation. This is saved for the user
    public void SaveRecent(){

        String toLanguage = _toLanguageSpinner.getSelectedItem().toString();
        if ( IsDetectLanguageSelected()){
            db.insertRECENT("Detect",toLanguage,GetInputText(),GetOutputText());
            recentList = db.FetchRecent();


        }else{
            db.insertRECENT(_fromLanguageCode,toLanguage,GetInputText(),GetOutputText());
            recentList = db.FetchRecent();
        }
    }

    // Fetches saved translations from user
    public void fetchTranslations(){
        recentList = db.FetchRecent();
        favouriteList = db.FetchFavourite();
    }

    // Handles when user clicks on settings
    public void handleSettingsClick(View view) {
      mStartForResult.launch(new Intent(this, Settings.class));
    }

    // Handles when user clicks on recents
    public void handleRecentClick(View view) {
        mStartForResult.launch(new Intent(this, Recents.class));
    }

    // Handles when user clicks on favourites
    public void handleFavouritesClick(View view) {
        mStartForResult.launch(new Intent(this, Favourites.class));
    }

    // Handles the switch between languages in the spinners
    public void handleLanguageSwitch(View view) {
        String fromLanguage = _fromLanguageSpinner.getSelectedItem().toString();

        // if detect, do nothing
        if (fromLanguage.equals("Detect")){
            return;
        }
        String toLanguage = _toLanguageSpinner.getSelectedItem().toString();


        _fromLanguageText.setText(fromLanguage);
        _toLanguageText.setText(toLanguage);

        _fromLanguageSpinner.setSelection(fromLanguageSpinnerAdapter.getPosition(toLanguage));
        _toLanguageSpinner.setSelection(toLanguageSpinnerAdapter.getPosition(fromLanguage));
    }

    // Handles when the user clicks on a translation in recents or favourites
    public void handleTranslationListElementClick(){

        String toLanguage = selectedTranslation.getTo_language();
        String fromLanguage = selectedTranslation.getFrom_language();

        // populates all widgets
        _fromLanguageText.setText(fromLanguage);
        _toLanguageText.setText(toLanguage);
        _fromLanguageSpinner.setSelection(fromLanguageSpinnerAdapter.getPosition(fromLanguage));
        _toLanguageSpinner.setSelection(toLanguageSpinnerAdapter.getPosition(toLanguage));
        _inputField.setText(selectedTranslation.getText());
        _outputField.setText(selectedTranslation.getTranslated_text());
    }

    //when the mic button is pressed
    private void OnMicButtonPressed(){
        //create an intent for speech-to-text recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //start the Speech-to-Text recognition activity
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    //when the speaker button is pressed
    private void OnSpeakerButtonPressed(){
        String text = _outputField.getText().toString(); //get the text from output field
        //if TTS is initialized (and text isnt empty)
        if (_tts != null && !text.isEmpty()) _tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop TTS when leaving page
        if (_tts != null){
            _tts.stop();
            _tts.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the activity is speech to text (and processed properly)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            //retrieve the recognized speech
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                //get the first result and set it to the input field
                String spokenText = results.get(0);
                _inputField.setText(spokenText);
            }
        }
    }
}




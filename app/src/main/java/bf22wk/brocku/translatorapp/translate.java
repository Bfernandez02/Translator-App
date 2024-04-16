package bf22wk.brocku.translatorapp;

public class translate {

    String from_language;
    String to_language;

    String text;
    String translated_text;

    public translate(String from, String to , String text, String translated){
        this.from_language = from;
        this.to_language = to;
        this.text = text;
        this.translated_text = translated;
    }


    public String getFrom_language() {
        return from_language;
    }

    public String getText() {
        return text;
    }

    public String getTo_language() {
        return to_language;
    }


    public String getTranslated_text() {
        return translated_text;
    }
}

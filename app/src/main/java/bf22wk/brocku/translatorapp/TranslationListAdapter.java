package bf22wk.brocku.translatorapp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.cloud.translate.v3.Translation;

import java.util.List;

//public class TranslationListAdapter extends ArrayAdapter<String> {
//
//    private final Activity context;
//    private final List<Translation> translationsList;
//
//    public TranslationListAdapter(Activity context, List<Translation> translationsList) {
//        super(context, R.layout.translation_cell, translationsList);
//        // TODO Auto-generated constructor stub
//
//        this.context=context;
//        this.translationsList=translationsList;
//
//    }
//
//    public View getView(int position, View view, ViewGroup parent) {
//        LayoutInflater inflater=context.getLayoutInflater();
//        View rowView=inflater.inflate(R.layout.mylist, null,true);
//
//        TextView titleText = (TextView) rowView.findViewById(R.id.title);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
//
//        titleText.setText(maintitle[position]);
//        imageView.setImageResource(imgid[position]);
//        subtitleText.setText(subtitle[position]);
//
//        return rowView;
//
//    };
//}

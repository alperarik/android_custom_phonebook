package com.example.alper_arik.smart_phonebook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Person>  implements Filterable{
    private Context mcontext;

    //Constructor
    public CustomAdapter(Context context, ArrayList<Person> people) {
        super(context,R.layout.custom_row, people);
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        final Person person = getItem(position);

        ImageView customRowPhoto = (ImageView) customView.findViewById(R.id.customRowPhoto);
        TextView customRowNameTextView = (TextView) customView.findViewById(R.id.customRowNameTextView);
        TextView customRowMobilePhoneTextView = (TextView) customView.findViewById(R.id.customRowMobilePhoneTextView);
        ImageView customRowCall = (ImageView) customView.findViewById(R.id.customRowCall);
        ImageView customRowMessage = (ImageView) customView.findViewById(R.id.customRowMessage);
        ImageView customRowLocation = (ImageView) customView.findViewById(R.id.customRowLocation);


        customRowCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Call",Toast.LENGTH_SHORT).show();
                if(mcontext instanceof MainActivity){
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:" + person.get_mobilePhoneNumber()));
                    try{
                        mcontext.startActivity(i);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });

        customRowMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mcontext,SendSMSActivity.class);
                i.putExtra("PHONE_NUMBER", person.get_mobilePhoneNumber());
                i.putExtra("PERSON_NAME", person.get_name());
                mcontext.startActivity(i);
            }
        });

        customRowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coor = FileOperation.readFromFile(person.get_mobilePhoneNumber() + "_LOCATION", mcontext);
                if(coor.equalsIgnoreCase("")){
                    Toast.makeText(mcontext, "There is not location info!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String [] tmp = coor.split("#");
                String [] parsedCoor = tmp[0].split(";");
                if(parsedCoor[0].equalsIgnoreCase("0.0") && parsedCoor[1].equalsIgnoreCase("0.0")){
                    Toast.makeText(mcontext, "There is not location info!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String location = "geo:"+parsedCoor[0]+","+parsedCoor[1];
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(location));
                i.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                mcontext.startActivity(i);

            }
        });

        customRowNameTextView.setText(person.get_name());
        customRowMobilePhoneTextView.setText(person.get_mobilePhoneNumber());
        return  customView;
    }

}

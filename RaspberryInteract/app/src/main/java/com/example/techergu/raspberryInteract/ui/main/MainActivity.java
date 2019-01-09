package com.example.techergu.raspberryInteract.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.techergu.raspberryInteract.ui.scan.DeviceList;
import com.example.techergu.raspberryInteract.R;

public class MainActivity extends AppCompatActivity {

    private MaterialDialog.Builder pommeDialog;
    private Boolean eaten =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();

        /** Test with apple
        pommeDialog = new MaterialDialog.Builder(this)
                .title("IMPORTANT")
                .content("Voulez vous vraiment créer la pomme ?")
                .positiveText("Oui").negativeText("Non")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        createPomme(context);
                    }
                });

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(l -> {pommeDialog.show();});
        */

        Button change = (Button) findViewById(R.id.change);
        change.setOnClickListener(l -> {startActivity( new Intent(MainActivity.this, DeviceList.class));});

        Button web = (Button) findViewById(R.id.web);
        web.setOnClickListener(l -> {startActivity(ActionActivity.getStartIntent(this));});

    }


    /**
     * Test create apple with click on popup
     * @param context

    public void createPomme(Context context) {
        TextView textView = (TextView) findViewById(R.id.appel);
        ImageView laPomme = (ImageView) findViewById(R.id.pomme);
        ImageView laPommeMange = (ImageView) findViewById(R.id.pommeMange);
        if (textView.getVisibility() == View.INVISIBLE && !this.eaten) {
            textView.setText("Pas de pomme !");
            laPomme.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            laPommeMange.setVisibility(View.INVISIBLE);
            this.pommeDialog.content("Voulez vous vraiment créer la pomme ?");
        } else if (textView.getVisibility() == View.VISIBLE && !this.eaten){
            textView.setVisibility(View.INVISIBLE);
            laPomme.setVisibility(View.VISIBLE);
            Toast.makeText(context, "HERE COME THE POMME", Toast.LENGTH_SHORT).show();
            this.pommeDialog.content("Voulez vous vraiment manger la pomme ?");
            this.eaten = true;
        } else if(this.eaten){
            Toast.makeText(context, "MIAM !", Toast.LENGTH_SHORT).show();
            laPomme.setVisibility(View.INVISIBLE);
            laPommeMange.setVisibility(View.VISIBLE);
            this.pommeDialog.content("Voulez vous vraiment faire disparaire la pomme ?");
            this.eaten = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    **/
}

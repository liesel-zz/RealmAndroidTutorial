package realmandroid.cafecomjava.com.br.realmandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Button btnSalvar;
    private EditText txtNome;
    private EditText txtEmail;
    private ListView listUsers;
    private ArrayList<User> users;
    private ArrayAdapter<User> usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.users = new ArrayList<User>();
        this.txtEmail   = (EditText) findViewById(R.id.txtEmail);
        this.txtNome    = (EditText) findViewById(R.id.txtNome);
        this.btnSalvar = (Button) findViewById(R.id.btnSave);
        this.listUsers = (ListView) findViewById(R.id.listUsers);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm realm = Realm.getInstance(realmConfig);

        RealmResults<User> realmUsers       = realm.where(User.class).findAll();
        for (User user: realmUsers){
            User us = new User();
            us.setNome(user.getNome());
            us.setEmail(user.getEmail());
            us.setId(user.getId());
            users.add(us);
        }
        realm.close();

        usersAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, users);
        this.listUsers.setAdapter(usersAdapter);
        this.listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm realm = Realm.getInstance(realmConfig);
                User usersDel = realm.where(User.class).equalTo("email",users.get(i).getEmail()) .findFirst();
                realm.beginTransaction();
                usersDel.deleteFromRealm();
                realm.commitTransaction();
                users.remove(i);
                usersAdapter.clear();
                usersAdapter.addAll(users);
                usersAdapter.notifyDataSetChanged();
                realm.close();
            }
        });

        this.btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtEmail.getText().toString().equalsIgnoreCase("") || txtNome.getText().toString().equalsIgnoreCase("")){
                    return;
                }
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm realm = Realm.getInstance(realmConfig);
                String email = txtEmail.getText().toString();
                String nome = txtNome.getText().toString();
                User user = realm.where(User.class).equalTo("email", email).findFirst();
                realm.beginTransaction();
                if (user != null){
                    user.setNome(nome);
                    user.setEmail(email);
                }else{
                    user = realm.createObject(User.class);
                    user.setId(users.size()+1);
                    user.setNome(nome);
                    user.setEmail(email);
                }
                realm.commitTransaction();
                realm.close();
                updateListView();
            }
        });
    }

    private void updateListView(){
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<User> realmUsers       = realm.where(User.class).findAll();
        this.usersAdapter.clear();
        users = new ArrayList<User>();
        for (User user: realmUsers){
            User us = new User();
            us.setNome(user.getNome());
            us.setEmail(user.getEmail());
            us.setId(user.getId());
            users.add(us);
        }
        this.usersAdapter.addAll(this.users);
        this.usersAdapter.notifyDataSetChanged();
        realm.close();
    }
}

package es.iessaladillo.pedrojoya.pr02_greetimproved;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import es.iessaladillo.pedrojoya.pr02_greetimproved.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding b;
    private TextWatcher txtNameTextWatcher;
    private TextWatcher txtSurnameTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        b = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupViews();

    }

    protected void onStart() {
        super.onStart();

        b.txtName.setOnFocusChangeListener((v, hasFocus) -> changeColor(b.textCharProgress1, hasFocus));
        b.txtSirname.setOnFocusChangeListener((v, hasFocus) -> changeColor(b.textCharProgress2, hasFocus));

        txtNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int actual_limit = b.txtName.getEditableText().length();
                b.textCharProgress1.setText(getResources().getQuantityString(R.plurals.char_limit_text, (20 -(20 - actual_limit)) , (20 -(20 - actual_limit))));

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateName(s.toString());
            }
        };
        b.txtName.addTextChangedListener(txtNameTextWatcher);

        txtSurnameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int actual_limit = b.txtSirname.getEditableText().length();
                b.textCharProgress2.setText(getResources().getQuantityString(R.plurals.char_limit_text, (20 -(20 - actual_limit)) , (20 -(20 - actual_limit))));
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateSurname(s.toString());
            }
        };
        b.txtSirname.addTextChangedListener(txtSurnameTextWatcher);
    }

    protected void onStop() {
        super.onStop();

        b.txtName.setOnFocusChangeListener(null);
        b.txtName.removeTextChangedListener(txtNameTextWatcher);

        b.txtSirname.setOnFocusChangeListener(null);
        b.txtSirname.removeTextChangedListener(txtSurnameTextWatcher);
    }

    //  Start values and functionality are initialized.
    private void setupViews() {

        //Funcionality
        b.swtPremium.setOnCheckedChangeListener((buttonView, isChecked) -> interleavingPremium(isChecked));
        b.rdgTreatment.setOnCheckedChangeListener((group, checkedId) -> updateImage(checkedId));
        b.clickButton.setOnClickListener(v -> incrementIfNotPremium());
        b.txtSirname.setOnEditorActionListener((v, actionId, event) -> txtSurnameOnEditorAction());

        //Values
        b.rdb01.setChecked(true);
        b.txtName.requestFocus();
        b.textProgress.setText(getString(R.string.progress_bar_text, b.progressBar.getProgress(), b.progressBar.getMax()));
        b.textCharProgress1.setText(getResources().getQuantityString(R.plurals.char_limit_text, 20,20));
        b.textCharProgress2.setText(getResources().getQuantityString(R.plurals.char_limit_text, 20,20));
        changeColor(b.textCharProgress1, true);

    }

    //  1. Validate the name.
    //      1.1. If the name is wrong it returns false, a visual error is thrown to the user and the focus is assigned.
    //      2.2 If the name is correct it returns true.
    private boolean validateName(String name) {
        if (!TextUtils.isEmpty(name)) {
            b.txtName.setError(null);
            return true;
        } else {
            b.txtName.setError(getString(R.string.main_required));
            b.txtName.requestFocus();
            return false;
        }
    }

    //  1. Validate the surname.
    //      1.1. If the surname is wrong it returns false, a visual error is thrown to the user and the focus is assigned.
    //      2.2 If the surname is correct it returns true.
    private boolean validateSurname(String surname) {
        if (!TextUtils.isEmpty(surname)) {
            b.txtSirname.setError(null);
            return true;
        } else {
            b.txtSirname.setError(getString(R.string.main_required));
            b.txtSirname.requestFocus();
            return false;
        }
    }

    //  Validate the form by checking if the name or surname is correct.
    private void isValidForm(String name, String surname) {
        if (validateName(name) && validateSurname(surname)) {
            incrementIfNotPremium();
        }

    }

    //  It does the same as the "Greet" button but called from the button created on the virtual keyboard.
    private boolean txtSurnameOnEditorAction() {
        isValidForm(b.txtName.getText().toString(), b.txtSirname.getText().toString());
        return true;

    }

    // Change the color of the focused elements by the usser.
    private void changeColor(TextView textView, boolean hasFocus) {
        int colorResId = hasFocus ? R.color.colorAccent : R.color.textPrimary;
        textView.setTextColor(ContextCompat.getColor(this, colorResId));

    }

    //  1. Each time the premium is activated the progress is restarted and the progress bar disappears.
    //  2. When we return to the non-premium, the progress bar reappears.
    private void interleavingPremium(boolean isChecked) {

        if (isChecked) {
            b.progressBar.setProgress(0);
            b.textProgress.setText(getString(R.string.progress_bar_text, b.progressBar.getProgress(), b.progressBar.getMax()));
            b.progressBar.setVisibility(View.GONE);
            b.textProgress.setVisibility(View.GONE);
        }else{
            b.progressBar.setVisibility(View.VISIBLE);
            b.textProgress.setVisibility(View.VISIBLE);
        }

    }

    //  1. Check if you are premium or not, and hide the virtual keyboard.
    //      1.1. If you are not premium, the usage counter increases to its limit and then displays a warning message.
    //      1.2. If it is premium, the counter disappears.
    private void incrementIfNotPremium() {

        int counter = b.progressBar.getProgress();
        String name = b.txtName.getText().toString();
        String surname = b.txtSirname.getText().toString();

        if(validateName(name) && validateSurname(surname)){
            SoftInputUtils.hideSoftKeyboard(b.textGreet);

            if(!b.swtPremium.isChecked()){
                if(counter >= b.progressBar.getMax()){
                    Toast.makeText(this, R.string.buy_premium_text, Toast.LENGTH_SHORT).show();

                }else{
                    showGreet();
                    counter++;
                    b.progressBar.setProgress(counter);
                    b.textProgress.setText(getString(R.string.progress_bar_text, counter, b.progressBar.getMax()));
                }
            }else{
                showGreet();
            }
        }


    }

    //  When I press the button I collect the information given (name, surname and treatment), launch a greeting.
    private void showGreet() {

        RadioButton seleccted_rb = findViewById(b.rdgTreatment.getCheckedRadioButtonId());

        String treatment = seleccted_rb.getText().toString();
        String name = b.txtName.getText().toString();
        String surname = b.txtSirname.getText().toString();

        if(b.chkPolitely.isChecked()) {
            Toast.makeText(this, getString(R.string.menssage_politely_text,treatment,name,surname), Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, getString(R.string.menssage_colloquially_text,name,surname), Toast.LENGTH_SHORT).show();
        }

    }

    //  Updates the image when the selected radiobutton is changed.
    private void updateImage(int checkedId) {

        if(checkedId == b.rdb01.getId()){
            b.imgPhoto.setImageResource(R.drawable.ic_mr);
        }
        else if(checkedId == b.rdb02.getId()){
            b.imgPhoto.setImageResource(R.drawable.ic_mrs);
        }else{
            b.imgPhoto.setImageResource(R.drawable.ic_ms);
        }

    }

}
package com.example.electricitycalculatorapplication;
import android.view.MenuItem;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    EditText editTextUnits, editTextRebate;
    TextView textViewResult;
    Button buttonCalculate, buttonClear;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();

        if (selected == R.id.menuAbout) {
            Toast.makeText(this, "about clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (selected == R.id.menuHome) {
            Toast.makeText(this, "home clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_about);

        editTextUnits = findViewById(R.id.editTextUnits);
        editTextRebate = findViewById(R.id.editTextRebate);
        textViewResult = findViewById(R.id.textViewResult);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonClear = findViewById(R.id.buttonClear);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBill();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear EditText fields
                editTextUnits.setText("");
                editTextRebate.setText("");
            }
        });

        // Set input type for editTextUnits and editTextRebate to allow only numbers
        editTextUnits.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTextRebate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Add TextWatchers to validate input instantly
        editTextUnits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInput(editTextUnits);
            }
        });

        editTextRebate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInput(editTextRebate);
            }
        });
    }

    private void validateInput(EditText editText) {
        String input = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(input)) {
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    editText.setError("Please enter a non-negative number");
                } else if (editText == editTextRebate && (value < 0 || value > 5)) {
                    editText.setError("Please enter a number between 0 and 5");
                } else {
                    editText.setError(null);
                }
            } catch (NumberFormatException e) {
                editText.setError("Please enter a valid number");
            }
        }
    }

    private void calculateBill() {
        // Check for errors before proceeding
        if (editTextUnits.getError() != null || editTextRebate.getError() != null) {
            return;
        }

        // Retrieve and convert input values
        double unitsUsed = Double.parseDouble(editTextUnits.getText().toString());
        double rebatePercentage = Double.parseDouble(editTextRebate.getText().toString());

        // Calculating charges based on unit slabs
        double charges = 0;
        if (unitsUsed <= 200) {
            charges = unitsUsed * 0.218;
        } else if (unitsUsed <= 300) {
            charges = 200 * 0.218 + (unitsUsed - 200) * 0.334;
        } else if (unitsUsed <= 600) {
            charges = 200 * 0.218 + 100 * 0.334 + (unitsUsed - 300) * 0.516;
        } else {
            charges = 200 * 0.218 + 100 * 0.334 + 300 * 0.515 + (unitsUsed - 600) * 0.546;
        }

        // Applying rebate and calculating final charges
        double rebateAmount = charges * rebatePercentage / 100;
        double finalCharges = charges - rebateAmount;

        // Displaying result
        textViewResult.setText(String.format("Electricity Bill after rebate: RM %.2f", finalCharges));
    }
}

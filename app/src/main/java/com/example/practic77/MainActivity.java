package com.example.practic77;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private EditText titleText, authorText;
    private Button addButton, updateButton, deleteButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private String selectedBookTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getBookTitles());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedBookTitle = adapter.getItem(position);
            Book book = Paper.book().read(selectedBookTitle, null);
            if (book != null) {
                titleText.setText(book.getTitle());
                authorText.setText(book.getAuthor());
            }
        });

        addButton.setOnClickListener(v -> {
            String title = titleText.getText().toString().trim();
            String author = authorText.getText().toString().trim();
            if (title.isEmpty() || author.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните оба поля!", Toast.LENGTH_SHORT).show();
                return;
            }
            Book book = new Book(title, title, author, null);
            Paper.book().write(title, book);
            updateBookList();
            clearInputs();
            Toast.makeText(MainActivity.this, "Книга добавлена!", Toast.LENGTH_SHORT).show();
        });

        updateButton.setOnClickListener(v -> {
            if (selectedBookTitle == null) {
                Toast.makeText(MainActivity.this, "Сначала выберите книгу!", Toast.LENGTH_SHORT).show();
                return;
            }
            String newTitle = titleText.getText().toString().trim();
            String newAuthor = authorText.getText().toString().trim();
            if (newTitle.isEmpty() || newAuthor.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните оба поля!", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedBookTitle);
            Book updatedBook = new Book(newTitle, newTitle, newAuthor, null);
            Paper.book().write(newTitle, updatedBook);
            updateBookList();
            clearInputs();
            Toast.makeText(MainActivity.this, "Книга обновлена!", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedBookTitle == null) {
                Toast.makeText(MainActivity.this, "Сначала выберите книгу!", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedBookTitle);
            updateBookList();
            clearInputs();
            Toast.makeText(MainActivity.this, "Книга удалена!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateBookList() {
        adapter.clear();
        adapter.addAll(getBookTitles());
        adapter.notifyDataSetChanged();
    }

    private List<String> getBookTitles() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private void clearInputs() {
        titleText.setText("");
        authorText.setText("");
        selectedBookTitle = null;
    }
}

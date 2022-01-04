package def.fhswf.ma.minesweeper;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class AnleitungActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anleitung);
        PDFView pdfView = findViewById(R.id.pdfView);

        pdfView.fitToWidth(1);
        pdfView.fromAsset("Hilfe.pdf")
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(0)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth(1);
                    }
                })
                .load();

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
}
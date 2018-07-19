package ua.artem.lymansky.slots;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ua.artem.lymansky.slots.R;

public class MainActivity extends AppCompatActivity {

    private int comboNumber = 7;
    private int coef1 = 72;
    private int coef2 = 142;
    private int coef3 = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    private SpinnerAdapter adapter;

    private ImageButton spinButton;
    private ImageButton plusButton;
    private ImageButton minusButton;
    private ImageButton settingsButton;
    private TextView jackpot;
    private TextView myCoins;
    private TextView lines;
    private TextView bet;
    private TextView winCoins;

    private LayoutInflater splashInflater;
    private View view;

    private Game game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Initializations
        splashInflater = getLayoutInflater();
        view = splashInflater.inflate(R.layout.win_splash,
                (ViewGroup) findViewById(R.id.win_coin_container));

        winCoins = findViewById(R.id.win_coins);
        game = new Game();
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        settingsButton = findViewById(R.id.settingsButton);
        jackpot = findViewById(R.id.jackpot);
        myCoins = findViewById(R.id.myCoins);
        lines = findViewById(R.id.lines);
        bet = findViewById(R.id.bet);
        adapter = new SpinnerAdapter();

        //RecyclerView settings
        rv1 = findViewById(R.id.spinner1);
        rv2 = findViewById(R.id.spinner2);
        rv3 = findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv3.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.setActivated(false);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        updateText();

        //RecyclerView listeners
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv1.scrollToPosition(game.getPosition(0));
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv2.scrollToPosition(game.getPosition(1));
                }
            }
        });

        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        rv3.scrollToPosition(game.getPosition(2));
                        updateText();
                        if (game.getHasWon()) {


                            Toast toast = new Toast(MainActivity.this);
                            toast.setView(view);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            game.setHasWon(false);
                            //TODO: set up win splash
                        }
                }
            }
        });

        //Button listeners
        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.getSpinResults();

                position1 = game.getPosition(0) + coef1;
                position2 = game.getPosition(1) + coef2;
                position3 = game.getPosition(2) + coef3;
                rv1.smoothScrollToPosition(position1);
                rv2.smoothScrollToPosition(position2);
                rv3.smoothScrollToPosition(position3);
                Log.e("Game.class-->", "Balance is " + game.getMyCoins() + ", the prize is " + game.getPrize());
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.betUp();
                updateText();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.betDown();
                updateText();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new Game();
                setInitialSlots();
                updateText();
            }
        });
    }

    private void setInitialSlots() {
        position1 = 5;
        position2 = 5;
        position3 = 5;
        rv1.smoothScrollToPosition(position1);
        rv2.smoothScrollToPosition(position2);
        rv3.smoothScrollToPosition(position3);
    }

    private void updateText() {
        jackpot.setText(game.getJackpot());
        myCoins.setText(game.getMyCoins());
        lines.setText(game.getLines());
        bet.setText(game.getBet());
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % comboNumber;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combination_1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combination_2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combination_3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combination_4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combination_5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combination_6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combination_7);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }
}


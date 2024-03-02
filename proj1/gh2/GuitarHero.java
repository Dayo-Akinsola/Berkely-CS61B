package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static GuitarString[] createKeyboard (String keyboard) {
        GuitarString[] keyboardArr = new GuitarString[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++) {
            double concert = CONCERT_A * Math.pow(2, ((i+1) - 24) / 12);
            keyboardArr[i] = new GuitarString(concert);
        }
        return keyboardArr;
    }


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString stringA = new GuitarString(CONCERT_A);
        GuitarString stringC = new GuitarString(CONCERT_C);
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] keyboardArr = createKeyboard(keyboard);


        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIndex = keyboard.indexOf(key);

                if (key == 'a') {
                    stringA.pluck();
                } else if (key == 'c') {
                    stringC.pluck();
                } else {
                    if (keyIndex != -1) {
                        keyboardArr[keyIndex].pluck();
                    }
                }

            }

            /* compute the superposition of samples */
            double sample = stringA.sample() + stringC.sample();
            for (int i = 0; i < keyboardArr.length; i++) {
                sample += keyboardArr[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            stringA.tic();
            stringC.tic();
            for (int i = 0; i < keyboardArr.length; i++) {
                keyboardArr[i].tic();
            }
        }
    }
}

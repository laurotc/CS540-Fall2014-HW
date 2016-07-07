import java.util.*;

/**
 * Your implementation of a naive bayes classifier. Please implement all four
 * methods.
 */
public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    private double delta = 0.00001;
    private double p_spam;
    private double p_ham;
    private List<String> vocabulary;
    private Map<String, Integer> counts_given_spam;
    private Map<String, Integer> counts_given_ham;
    private int count_spam = 0;
    private int count_ham = 0;
    private int vocabSize = 0;
    

    @Override
    public void train(Instance[] trainingData, int v) {
        // Implement	
        this.vocabSize = v;
        int count_instance_spam = 0;
        int count_instance_ham = 0;

        this.vocabulary = new ArrayList<String>();
        this.counts_given_spam = new HashMap<String, Integer>(v);
        this.counts_given_ham = new HashMap<String, Integer>(v);

        for (Instance instance : trainingData) {
            if (instance.label.equals(Label.SPAM)) {
                count_instance_spam++;
            } else {
                count_instance_ham++;
            }

            for (String word : instance.words) {
                if (!this.vocabulary.contains(word)) {
                    this.vocabulary.add(word);
                }

                if (instance.label.equals(Label.SPAM)) {
                    this.count_spam++;
                    count_words_label(this.counts_given_spam, word);
                } else {
                    this.count_ham++;
                    count_words_label(this.counts_given_ham, word);
                }
            }
        }

        this.p_spam = (double) count_instance_spam / trainingData.length;
        this.p_ham = (double) count_instance_ham / trainingData.length;
    }

    public void count_words_label(Map<String, Integer> counts_given_label, String word) {
        if (!counts_given_label.containsKey(word)) {
            counts_given_label.put(word, 1);
        } else {
            counts_given_label.put(word, counts_given_label.get(word) + 1);
        }
    }

    @Override
    public double p_l(Label label) {
        // Implement
        if (label.equals(Label.HAM)) {
            return this.p_ham;
        } else {
            return this.p_spam;
        }
    }

    /**
     * Returns the smoothed conditional probability of the word given the label,
     * i.e. P(word|SPAM) or P(word|HAM)
     */
    @Override
    public double p_w_given_l(String word, Label label) {
        // Implement
        Integer number_words;
        Integer size;
        
        if (label == Label.SPAM) {
            number_words  = this.counts_given_spam.get(word);
            size = this.count_spam;
        } else {
            number_words = this.counts_given_ham.get(word);
            size = this.count_ham;
        }
        if (number_words == null) {
            number_words = 0;
        }
        return ((double) number_words + this.delta) / ((double) (this.vocabSize * this.delta) + size);

    }

    /**
     * Classifies an array of words as either SPAM or HAM.
     */
    @Override
    public ClassifyResult classify(String[] words) {
        // Implement
        ClassifyResult result = new ClassifyResult();

        result.log_prob_ham = Math.log(this.p_ham);
        result.log_prob_spam = Math.log(this.p_spam);

        for (String word : words) {
            result.log_prob_ham += Math.log(p_w_given_l(word, Label.HAM));
            result.log_prob_spam += Math.log(p_w_given_l(word, Label.SPAM));
        }

        if (result.log_prob_ham > result.log_prob_spam) {
            result.label = Label.HAM;
        } else if (result.log_prob_spam > result.log_prob_ham) {
            result.label = Label.SPAM;
        }

        return result;
    }
}


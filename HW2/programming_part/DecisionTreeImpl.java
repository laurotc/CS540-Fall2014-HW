import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 *
 * You must add code for the 5 methods specified below.
 *
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {

    private DecTreeNode root;
    private List<String> labels; // ordered list of class labels
    private List<String> attributes; // ordered list of attributes
    private Map<String, List<String>> attributeValues; // map to ordered
    // discrete values taken
    // by attributes

    /**
     * Answers static questions about decision trees.
     */
    DecisionTreeImpl() {
		// no code necessary
        // this is void purposefully
    }

    /**
     * Build a decision tree given only a training set.
     *
     * @param train: the training set
     */
    DecisionTreeImpl(DataSet train) {

        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        // TODO: add code here
        this.root = build_tree(train.instances, train.attributes, train.instances, -1, train);
    }

    /**
     * Build a decision tree given a training set then prune it using a tuning
     * set.
     *
     * @param train: the training set
     * @param tune: the tuning set
     */
    DecisionTreeImpl(DataSet train, DataSet tune) {

        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        // TODO: add code here
        this.root = build_tree(train.instances, train.attributes, train.instances, -1, train);
    }

    private DecTreeNode build_tree(List<Instance> instances, List<String> attributes, List<Instance> parentInstances, Integer parentAtt, DataSet train) {

        if (instances.isEmpty()) {
            // find most common class among parent examples
            Integer label = getMostCommonClass(parentInstances);
            return new DecTreeNode(label, null, parentAtt, true);
        } else if (findSameClass(instances)) {
            // get the remaining label
            return new DecTreeNode(instances.get(0).label, null, parentAtt, true);
        } else if (attributes.isEmpty()) {
            // find most common label
            Integer label = getMostCommonClass(instances);
            return new DecTreeNode(label, null, parentAtt, true);
        } else {
            List<String> info = informationGain(instances, attributes, train);
            DecTreeNode node = new DecTreeNode(Integer.parseInt(info.get(0)), Integer.parseInt(info.get(1)), parentAtt, false);
            String nodeAttr = this.attributes.get(node.attribute);
            List<String> newAttributes = new ArrayList<>();
            for (int i = 0; i < attributes.size(); i++) {
                if (i != node.attribute) {
                    newAttributes.add(attributes.get(i));
                }
            }
            for (int k = 0; k < this.attributeValues.get(nodeAttr).size(); k++) {
                List<Instance> exs = getExamples(instances, node.attribute, k + 1);
                DecTreeNode subtree = build_tree(exs, newAttributes, instances, k, train);
                node.children.add(subtree);
            }
            return node;
        }
    }

    /*
     * Find the instances with attr value k in the position attrIndex
     */
    private static List<Instance> getExamples(List<Instance> instances, int attrIndex, int k) {
        List<Instance> examples = new ArrayList<>();
        for (Instance inst : instances) {
            if (inst.attributes.get(attrIndex) == k) {
                examples.add(inst);
            }
        }
        return examples;
    }

    /**
     * Find instances with same class
     */
    private static boolean findSameClass(List<Instance> instances) {
        int label = -1;
        for (Instance inst : instances) {
            if (label < 0) {
                label = inst.label;
            } else if (label != inst.label) {
                return false;
            }
        }
        return true;
    }

    /*
     * Finds the attribute which will provide the highest information gain
     */
    private List<String> informationGain(List<Instance> instances, List<String> attributes, DataSet data) {
        // initialize information gain (will be maximized)
        double infoGain = -1;
        String mutInfoStr = "";
        int maxAttr = -1;
        int maxLabel = -1;
        String currAttrRet = "";
        // cycle attributes
        for (String currAttr : attributes) {
            // compute attribute entropy
            double H_Y = 0;
            double total = data.instances.size();
            int currIndex = -1;
            for (int i = 0; i < data.attributes.size(); i++) {
                if (data.attributes.get(i).equalsIgnoreCase(currAttr)) {
                    currIndex = i;
                }
            }
            if (currIndex < 0) {
                continue;
            }
            double[] labels = new double[data.labels.size()];
            for (Instance inst : instances) {
                labels[inst.label]++;
            }
            for (double labelCount : labels) {
                H_Y += -(labelCount / total) * (Math.log(labelCount / total) / Math.log(2.0));
            }
            int currMaxLabel = -1;
            double currMaxLabelCounts = -1;
            for (int i = 0; i < data.labels.size(); i++) {
                if (labels[i] > currMaxLabelCounts) {
                    currMaxLabelCounts = labels[i];
                    currMaxLabel = i;
                }
            }
            // compute conditional entropy
            double[] HCond = new double[data.attributeValues.get(currAttr).size()];
            double[] attributeVals = new double[data.attributeValues.get(currAttr).size()];
            double[][] labelVals = new double[attributeVals.length][data.labels.size()];
            for (int i = 0; i < instances.size(); i++) {
                Instance currInst = instances.get(i);
                attributeVals[currInst.attributes.get(currIndex)]++;
                labelVals[currInst.attributes.get(currIndex)][currInst.label]++;
            }
            for (int i = 0; i < data.attributeValues.get(currAttr).size(); i++) {
                for (int j = 0; j < data.labels.size(); j++) {
                    if (labelVals[i][j] == 0 || attributeVals[i] == 0) {
                        continue;
                    }
                    HCond[i] += -(labelVals[i][j] / attributeVals[i]) * (Math.log(labelVals[i][j] / attributeVals[i]) / Math.log(2.0));
                }
                HCond[i] = HCond[i] * (attributeVals[i] / total);
            }
            double HCondTot = 0;
            for (double number : HCond) {
                HCondTot += number;
            }
            double mutInfo = H_Y - HCondTot;

            mutInfoStr = mutInfoStr + "," + mutInfo;
            currAttrRet = currAttrRet + "," + currAttr;

            if (mutInfo > infoGain) {
                infoGain = mutInfo;
                maxAttr = this.attributes.indexOf(currAttr);
                maxLabel = currMaxLabel;
            }
        }

        List<String> ret = new ArrayList<>();
        ret.add(maxLabel + "");
        ret.add(maxAttr + "");
        ret.add(mutInfoStr);
        ret.add(currAttrRet);

        return ret;
    }

    /*
     * Get the most common label in a group of Instance objects.
     */
    private Integer getMostCommonClass(List<Instance> instances) {
        int[] labelAppearances = new int[this.labels.size()];
        for (Instance inst : instances) {
            labelAppearances[inst.label]++;
        }
        int maxApp = -1;
        int maxLab = -1;
        for (int i = 0; i < labelAppearances.length; i++) {
            if (labelAppearances[i] > maxApp) {
                maxApp = labelAppearances[i];
                maxLab = i;
            }
        }
        return maxLab;
    }

    @Override
    public String classify(Instance instance) {

        return classifier(this.root, instance);
    }

    public String classifier(DecTreeNode node, Instance instance) {
        String classification = null;
        if (node.terminal) {
            return Integer.toString(node.label);
        }
        int attrIndex = -1;
        for (int i = 0; i < this.attributes.size(); i++) {
            if (this.attributes.get(i).equals(this.attributes.get(node.attribute))) {
                attrIndex = i;
                break;
            }
        }

        for (DecTreeNode child : node.children) {
            if (child.parentAttributeValue.equals(instance.attributes.get(attrIndex))) {
                classification = classifier(child, instance);
            }
        }
        return classification;
    }

    @Override
    /**
     * Print the decision tree in the specified format
     */
    public void print() {

        printTreeNode(root, null, 0);
    }

    /**
     * Prints the subtree of the node with each line prefixed by 4 * k spaces.
     */
    public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < k; i++) {
            sb.append("    ");
        }
        String value;
        if (parent == null) {
            value = "ROOT";
        } else {
            String parentAttribute = attributes.get(parent.attribute);
            value = attributeValues.get(parentAttribute).get(p.parentAttributeValue);
        }
        sb.append(value);
        if (p.terminal) {
            sb.append(" (" + labels.get(p.label) + ")");
            System.out.println(sb.toString());
        } else {
            sb.append(" {" + attributes.get(p.attribute) + "?}");
            System.out.println(sb.toString());
            for (DecTreeNode child : p.children) {
                printTreeNode(child, p, k + 1);
            }
        }
    }

    @Override
    public void rootInfoGain(DataSet train) {

        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        // TODO: add code here
        List<String> info = informationGain(train.instances, this.attributes, train);
        String[] mInfo = info.get(2).split("\\,");
        String[] attr = info.get(3).split("\\,");
        for (int i = 1; i < mInfo.length; i++) {
            System.out.format("%s %.5f\n", attr[i] + " ", Double.parseDouble(mInfo[i]));
        }
    }

}
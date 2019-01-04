package webmagic.bean;

import java.util.List;
//碎片和碎片内容
public class FragmentContent {


    List<String> fragments;
    List<String> fragmentsPureText;
    @Override
    public String toString() {
        return "FragmentContent{" +
                "fragments=" + fragments +
                ", fragmentsPureText=" + fragmentsPureText +
                '}';
    }

    public List<String> getFragments() {
        return fragments;
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }

    public List<String> getFragmentsPureText() {
        return fragmentsPureText;
    }

    public void setFragmentsPureText(List<String> fragmentsPureText) {
        this.fragmentsPureText = fragmentsPureText;
    }

    public FragmentContent() {

    }

    public FragmentContent(List<String> fragments, List<String> fragmentsPureText) {

        this.fragments = fragments;
        this.fragmentsPureText = fragmentsPureText;
    }

}

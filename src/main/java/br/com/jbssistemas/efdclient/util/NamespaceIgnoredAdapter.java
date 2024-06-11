package br.com.jbssistemas.efdclient.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NamespaceIgnoredAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return v;
    }

    @Override
    public String marshal(String v) throws Exception {
        return v;
    }

}

package org.dbpedia.browser;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Reads an rdf file from an input stream and saves interesting properties.
 */
public class RDFReader {
    private static final String TAG = "DBpediaBrowser";

    //Saves the values of the interesting properties found in the file.
    private HashMap<String, ArrayList<String>> interestingPropertiesValues;
    //Contains the interessting properties we're looking for
    private ArrayList<String> interestingProperties;
    private XmlPullParser mParser;
    //Saves the types of the object (e.g 'http://dbpedia.org/ontology/Location')
    private ArrayList<String> types;

    public RDFReader(Context context, InputStream inputStream) {
        interestingProperties = new ArrayList<>();
        interestingPropertiesValues = new HashMap<>();
        types = new ArrayList<>();
        try {
            XmlPullParserFactory mParserFactory = XmlPullParserFactory.newInstance();
            mParserFactory.setNamespaceAware(false);
            mParser = mParserFactory.newPullParser();
            mParser.setInput(new InputStreamReader(inputStream));
            //Get interesting properties from res/values/arrays.xml
            interestingProperties.addAll(Arrays.asList(context.getResources().getStringArray(R.array.interestingProperties)));
            init();
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getClass().getName() + " " + e.getLocalizedMessage());
        }
    }

    /**
     * Empty constructor - does nothing.
     */
    public RDFReader() {
        interestingProperties = new ArrayList<>();
        interestingPropertiesValues = new HashMap<>();
        types = new ArrayList<>();
    }


    /**
     * Reads the files once from top to bottom and saves all the interesting properties found to interestingPropertiesValues
     */
    private void init() {
        try {
            while (mParser.next() != XmlPullParser.END_DOCUMENT) {
                if (mParser.getEventType() == XmlPullParser.START_TAG && interestingProperties.contains(mParser.getName())) {
                    ArrayList<String> list = interestingPropertiesValues.get(mParser.getName());
                    if (list == null) {
                        list = new ArrayList<>();
                        interestingPropertiesValues.put(mParser.getName(), list);
                    }
                    //For tags in form: <tag xml:lang="xx">Text</tag> which have a localized text.
                    //English value is added to the first position as a fallback if there is no entry in the device's language
                    if (mParser.getName().equals("dbo:abstract") || mParser.getName().equals("rdfs:label")) {
                        if (mParser.getAttributeValue(null, "xml:lang").equals(Locale.getDefault().getLanguage()) ||
                                mParser.getAttributeValue(null, "xml:lang").equals("en")) {
                            if (mParser.getAttributeValue(null, "xml:lang").equals("en")) {
                                mParser.next();
                                list.add(0, mParser.getText());
                            } else {
                                mParser.next();
                                list.add(mParser.getText());
                            }
                        }
                    }
                    //For tags in form: <tag datatype="xx">Text</tag>
                    else if (mParser.getName().equals("dbp:locationCity") ||
                            mParser.getName().equals("georss:point") ||
                            mParser.getName().equals("dbo:birthDate") ||
                            mParser.getName().equals("dbo:deathDate") ||
                            mParser.getName().equals("dbo:populationTotal") ||
                            mParser.getName().equals("dbo:areaTotal")) {
                        mParser.next();
                        list.add(mParser.getText());
                    }
                    //For the rdf:type attribute
                    else if (mParser.getName().equals("rdf:type")) {
                        types.add(mParser.getAttributeValue(null, "rdf:resource"));
                    }
                    //For tags in normal form: <tag rdf:resource="xx"/>
                    else {
                        list.add(mParser.getAttributeValue(null, "rdf:resource"));
                    }
                }
            }


        } catch (XmlPullParserException | IOException ignored) {
        }
    }

    /**
     * @param propertyName The name of the property (e.g. 'dbo:label')
     * @return A list containing all values found for the given property, null if the list doesn't exist or is empty
     */
    public ArrayList<String> getResultsForProperty(String propertyName) {
        if (interestingPropertiesValues.get(propertyName) == null || interestingPropertiesValues.get(propertyName).size() == 0)
            return null;
        return interestingPropertiesValues.get(propertyName);
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}

package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.IndexImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesse Stuart
 */
public class MascJsonIndex implements Index
{
    protected Index index;

    public MascJsonIndex() throws IOException
    {
        index = new IndexImpl("masc3-json.index");
    }

    public File get(String id)
    {
        return index.get(id);
    }

    public List<String> keys()
    {
        return new ArrayList<String>(index.keys());
    }
}

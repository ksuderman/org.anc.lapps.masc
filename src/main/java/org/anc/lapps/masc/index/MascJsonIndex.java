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
public class MascJsonIndex extends MascIndex
{
    public MascJsonIndex() throws IOException
    {
        super("masc3-json.index");
    }
}

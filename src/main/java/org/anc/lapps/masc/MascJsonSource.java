package org.anc.lapps.masc;

import org.anc.lapps.masc.index.MascFullIndex;
import org.anc.lapps.masc.index.MascJsonIndex;
import org.lappsgrid.api.Data;
import org.lappsgrid.discriminator.Types;

import java.io.IOException;

/**
 *
 * @author Jesse Stuart
 */
public class MascJsonSource extends AbstractDataSource
{
    public MascJsonSource() throws IOException
    {
        super(new MascJsonIndex());
        System.out.println("Created a MASC text datasource.");
    }

    @Override
    /**
     * The text data source either returns an error or the actual text.
     */
    protected Data get(String key)
    {
        System.out.println("Getting text for " + key);
        Data result = super.get(key);
        if (result.getDiscriminator() != Types.ERROR)
        {
            result.setDiscriminator(Types.TEXT);
        }
        return result;
    }
}

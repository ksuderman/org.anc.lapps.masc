package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascAbstractDataSource;
import org.anc.lapps.masc.MascLifSource;
import org.anc.lapps.masc.Version;
import org.junit.*;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import static org.lappsgrid.discriminator.Discriminators.Uri;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */
public class MascLifSourceTest
{
	protected MascLifSource source;

	public MascLifSourceTest()
	{

	}

	@Before
	public void setup() throws IOException
	{
		MascAbstractDataSource.testing = true;
		source = new MascLifSource();
	}

	@After
	public void cleanup()
	{
		source = null;
	}

	@Test
	public void testList()
	{
		String json = new Data<String>(Uri.LIST, null).asJson();
		json = source.execute(json);
		Data data = Serializer.parse(json, Data.class);
		not(data, Uri.ERROR);
		is(data, Uri.STRING_LIST);
		List<String> listing = (List<String>) data.getPayload();
		assertEquals(392, listing.size());
	}

	@Test
	public void testParameterizedList()
	{
		Map<String,Integer> offsets = new HashMap<>();
		offsets.put("start", 0);
		offsets.put("end", 10);
		String json = new Data<Map>(Uri.LIST, offsets).asJson();
		json = source.execute(json);
		Data data = Serializer.parse(json, Data.class);
		not(data, Uri.ERROR);
		is(data, Uri.STRING_LIST);

		List<String> listing = (List<String>) data.getPayload();
		assertEquals(10, listing.size());
	}

	@Test
	public void testGet()
	{
//		String json = DataFactory.get("MASC3-0202");
		String json = new Data<String>(Uri.GET, "MASC3-0202").asJson();
		json = source.execute(json);
		Data data = Serializer.parse(json, Data.class);
		not(data, Uri.ERROR);
		is(data, Uri.LAPPS);
		Container container = new Container((Map) data.getPayload());
		assertEquals("en-US", container.getLanguage());
		assertNotNull(container.getText());
	}

	@Test
	public void testMetadata()
	{
		String json = source.getMetadata();
		Data data = Serializer.parse(json, Data.class);
		not(data, Uri.ERROR);
		is(data, Uri.META);
		DataSourceMetadata metadata = new DataSourceMetadata((Map) data.getPayload());
		assertEquals("http://www.anc.org", metadata.getVendor());
		assertEquals(MascLifSource.class.getName(), metadata.getName());
		assertEquals(Version.getVersion(), metadata.getVersion());
		assertEquals(1, metadata.getFormat().size());
		assertEquals(Uri.LAPPS, metadata.getFormat().get(0));
		assertEquals(1, metadata.getLanguage().size());
		assertEquals("en-US", metadata.getLanguage().get(0));
		assertEquals(Uri.CC_BY, metadata.getLicense());
	}
	private void not(Data data, String type)
	{
		assertFalse(type.equals(data.getDiscriminator()));
	}

	private void is(Data data, String type)
	{
		assertEquals(type, data.getDiscriminator());
	}
}

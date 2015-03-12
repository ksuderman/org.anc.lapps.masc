package org.anc.lapps.masc.test;

import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.anc.json.validator.Validator;
import org.anc.lapps.masc.MascJsonSource;
import org.anc.lapps.masc.MascTextSource;
import org.anc.lapps.masc.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
public class MetadataTests
{
	protected Validator validator;

	public MetadataTests()
	{

	}

	@Before
	public void setup()
	{
		URL url = this.getClass().getResource("/datasource-schema.json");
		validator = new Validator(url);
	}

	@After
	public void cleanup()
	{
		validator = null;
	}

	@Test
	public void testMascJsonSource() throws IOException
	{
		WebService service = new MascJsonSource();
		String json = service.getMetadata();
		Data<String> data = Serializer.parse(json, Data.class);
		assertEquals(data.getDiscriminator(), Uri.META);
		json = data.getPayload();

		ProcessingReport report = validator.validate(json);
		if (!report.isSuccess())
		{
			for (ProcessingMessage message : report)
			{
				System.out.println(message.getMessage());
				fail("Validationg failed.");
			}
		}

		DataSourceMetadata metadata = Serializer.parse(json, DataSourceMetadata.class);
		assertEquals(Version.getVersion(), metadata.getVersion());
		assertEquals(MascJsonSource.class.getCanonicalName(), metadata.getName());
	}

	@Test
	public void testMascTextSource() throws IOException
	{
		WebService service = new MascTextSource();
		String json = service.getMetadata();
		Data<String> data = Serializer.parse(json, Data.class);
		assertEquals(data.getDiscriminator(), Uri.META);
		json = data.getPayload();

		ProcessingReport report = validator.validate(json);
		if (!report.isSuccess())
		{
			for (ProcessingMessage message : report)
			{
				System.out.println(message.getMessage());
				fail("Validationg failed.");
			}
		}

		DataSourceMetadata metadata = Serializer.parse(json, DataSourceMetadata.class);
		assertEquals(Version.getVersion(), metadata.getVersion());
		assertEquals(MascTextSource.class.getCanonicalName(), metadata.getName());
	}
}

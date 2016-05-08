package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.IndexImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Keith Suderman
 */
public abstract class MascIndex implements Index
{
	protected IndexImpl index;

	public MascIndex(String name) throws IOException
	{
		index = new IndexImpl(name);
	}

	@Override
	public int size()
	{
		return index.size();
	}

	@Override
	public File get(String id)
	{
		return index.get(id);
	}

	@Override
	public List<String> keys()
	{
		return new ArrayList<String>(index.keys());
	}

	public List<IndexImpl.Entry> entries() { return index.entries(); }

	public List<String> query(String pattern)
	{
		List<String> list = new ArrayList<>();
		for (IndexImpl.Entry entry : index)
		{
			if (entry.getPath().contains(pattern))
			{
				list.add(entry.getId());
			}
		}
		return list;
	}

}

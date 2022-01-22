/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itsaky.inflater.values;

import com.itsaky.androidide.utils.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A factory class that reads the files in the "values" directory of given "res" directories.
 * @author Akash Yadav
 */
public class ValuesTableFactory {
    
    private static final Map<File, ValuesTable> valueTables = new HashMap<> ();
    private static final Logger LOG = Logger.instance ("ValuesTableFactory");
    
    public static ValuesTable getTable (final File resDir) {
        return valueTables.getOrDefault (resDir, null);
    }
    
    /**
     * Setup this ValuesTableFactory with the given "res" directories.
     * @param resDirs The resource directories.
     */
    public static void setupWithResDirectories (File... resDirs) {
    
        if (resDirs == null || resDirs.length <= 0) {
            LOG.error ("Cannot create value tables. No directories were specified.");
            return;
        }
        
        valueTables.clear ();
        
        final var start = System.currentTimeMillis ();
        for (var res : resDirs) {
            if (res == null || !res.exists ()) {
                continue;
            }
            
            final var table = createTable (res);
            if (table == null) {
                continue;
            }
            
            valueTables.put (res, table);
        }
        
        LOG.debug (String.format (Locale.getDefault (), "Created value tables for %d resource directories in %d ms", resDirs.length, (System.currentTimeMillis () - start)));
    }
    
    private static ValuesTable createTable (File res) {
        try {
            final var values = new File (res, "values");
            if (!values.exists ()) {
                return null;
            }
            
            var table = ValuesTable.forDirectory (values);
            if (table == null) {
                throw new ParseException ();
            }
            
            return table;
        } catch (Throwable e) {
            LOG.error ("Failed to create values table", e);
            return null;
        }
    }
    
    static class ParseException extends RuntimeException {}
}

/**

 The ISAconverter, ISAvalidator & BII Management Tool are components of the ISA software suite (http://www.isa-tools.org)

 Exhibit A
 The ISAconverter, ISAvalidator & BII Management Tool are licensed under the Mozilla Public License (MPL) version
 1.1/GPL version 2.0/LGPL version 2.1

 "The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"). You may not use this file except in compliance with the License.
 You may obtain copies of the Licenses at http://www.mozilla.org/MPL/MPL-1.1.html.

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations
 under the License.

 The Original Code is the ISAconverter, ISAvalidator & BII Management Tool.

 The Initial Developer of the Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are Copyright (c)
 2007-2011 ISA Team. All Rights Reserved.

 Contributor(s):
 Rocca-Serra P, Brandizi M, Maguire E, Sklyar N, Taylor C, Begley K, Field D,
 Harris S, Hide W, Hofmann O, Neumann S, Sterk P, Tong W, Sansone SA. ISA software suite:
 supporting standards-compliant experimental annotation and enabling curation at the community level.
 Bioinformatics 2010;26(18):2354-6.

 Alternatively, the contents of this file may be used under the terms of either the GNU General
 Public License Version 2 or later (the "GPL") - http://www.gnu.org/licenses/gpl-2.0.html, or
 the GNU Lesser General Public License Version 2.1 or later (the "LGPL") -
 http://www.gnu.org/licenses/lgpl-2.1.html, in which case the provisions of the GPL
 or the LGPL are applicable instead of those above. If you wish to allow use of your version
 of this file only under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your decision by deleting
 the provisions above and replace them with the notice and other provisions required by the
 GPL or the LGPL. If you do not delete the provisions above, a recipient may use your version
 of this file under the terms of any one of the MPL, the GPL or the LGPL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project
 (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC
 (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).

 */

package org.isatools.isatab.export.properties;


import org.apache.commons.lang.StringUtils;
import org.isatools.tablib.export.TabExportUtils;
import org.isatools.tablib.schema.Field;
import org.isatools.tablib.schema.Record;
import org.isatools.tablib.schema.Section;
import org.isatools.tablib.schema.SectionInstance;
import org.isatools.tablib.utils.BIIObjectStore;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.utils.i18n;

/**
 * A utility used in several points to export a {@link } object.
 * <p/>
 * date: Apr 7, 2008
 *
 * @author brandizi
 */
public class OntologyEntryExportingUtility {
    private final BIIObjectStore store;
    private final String ontologyEntryFieldName, accessionFieldName, sourceFieldName;
    private int fieldIndex = -1;

    /**
     * Initializes the tool, which can later be used with {@link #export(OET, Record)}.
     *
     * @param ontologyEntryFieldName the field which will store the first ontology entry attached to the term, for instance
     *                               "Experimental Factor Type".
     * @param accessionFieldName     the field which reports the term accession, e.g.: "Factor Type Accession Number".
     *                               Accession is always omitted when this parameter is null.
     * @param sourceFieldName        the field which reports the term source. Must be non null if previous two are not too.
     * @patam store it's used to add the term's source to it.
     */
    public OntologyEntryExportingUtility(
            BIIObjectStore store, String ontologyEntryFieldName, String accessionFieldName, String sourceFieldName
    ) {
        this.store = store;
        this.ontologyEntryFieldName = ontologyEntryFieldName;
        this.accessionFieldName = accessionFieldName;
        this.sourceFieldName = sourceFieldName;
    }


    public <OET extends OntologyEntry> Record export(OET oe, Record record) {
        TabExportUtils.OEString oestr = TabExportUtils.storeSource(store, oe);
        String termLabel = oestr.label, termAccession = oestr.acc, termSource = oestr.src;

        int fieldIndex = this.fieldIndex;

        record.set(fieldIndex, StringUtils.trimToEmpty(termLabel));
        if (this.accessionFieldName != null) {
            record.set(fieldIndex + 1, StringUtils.trimToEmpty(termAccession));
        }
        record.set(fieldIndex + 2, StringUtils.trimToEmpty(termSource));

        return record;
    }


    public SectionInstance setFields(SectionInstance sectionInstance) {
        Section section = sectionInstance.getSection();

        if (ontologyEntryFieldName == null) {
            throw new RuntimeException(i18n.msg("missing_oe_field_name"));
        }

        Field field = new Field(ontologyEntryFieldName);
        field.setSection(section);
        sectionInstance.addField(field);
        this.fieldIndex = field.getIndex();

        if (accessionFieldName != null) {
            field = new Field(accessionFieldName);
            field.setSection(section);
            sectionInstance.addField(field);
        }

        if (sourceFieldName == null) {
            throw new RuntimeException(i18n.msg("missing_oe_source_field_name", ontologyEntryFieldName));
        }

        //"Cannot export the ontology entry field '" + ontologyEntryFieldName + "' without a source field name"
        //);

        field = new Field(sourceFieldName);
        field.setSection(section);
        sectionInstance.addField(field);

        return sectionInstance;
    }
}

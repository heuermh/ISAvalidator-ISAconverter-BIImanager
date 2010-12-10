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

package org.isatools.tablib.schema.constraints;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.isatools.tablib.exceptions.TabInvalidValueException;
import org.isatools.tablib.exceptions.TabStructureError;
import org.isatools.tablib.schema.Field;
import uk.ac.ebi.bioinvindex.utils.i18n;

import java.util.Map;

/**
 * Defines how many occurences of a certain field are expected or allowed.
 *
 * @author brandizi
 *         <b>date</b>: May 29, 2009
 */
public class FieldCardinalityConstraint extends FieldConstraint {
    public static enum CardinalityType {
        MIN, MAX, EXACT
    }

    ;

    private final CardinalityType cardinalityType;
    private final int cardinality;

    public FieldCardinalityConstraint(boolean isMandatory, CardinalityType cardinalityType, int cardinality) {
        super(isMandatory);
        this.cardinalityType = cardinalityType;
        this.cardinality = cardinality;
    }


    public CardinalityType getCardinalityType() {
        return cardinalityType;
    }

    public int getCardinality() {
        return cardinality;
    }

    public static FieldCardinalityConstraint parseConstraint(Field field) {
        Map<String, String> attrs = field.getAttrs();
        String cards = attrs.get("cardinality");
        if (cards == null) {
            return null;
        }

        boolean isMandatory = true;
        if (cards.startsWith("supposed ")) {
            cards = cards.substring("supposed ".length());
            isMandatory = false;
        }
        CardinalityType type = CardinalityType.EXACT;
        if (cards.endsWith("+")) {
            type = CardinalityType.MIN;
            cards = cards.substring(0, cards.length() - 1);
        } else if (cards.endsWith("-")) {
            type = CardinalityType.MAX;
            cards = cards.substring(0, cards.length() - 1);
        }

        if (!NumberUtils.isDigits(cards)) {
            throw new TabInvalidValueException(i18n.msg(
                    "invalid_cardinality_constraint",
                    field.getId(),
                    field.getIndex(),
                    attrs.get("cardinality")
            ));
        }

        int cardVal = Integer.parseInt(cards);
        return new FieldCardinalityConstraint(isMandatory, type, cardVal);
    }


    public boolean validateCardinality(int cardValue, String fieldName, Logger log) {
        if (cardinalityType == CardinalityType.MIN && cardValue < cardinality) {
            if (isMandatory()) {
                throw new TabStructureError(
                        i18n.msg("cardinality_constraint_not_matched", "ERROR", fieldName, "must", "at least", cardinality
                        ));
            } else {
                log.warn(i18n.msg(
                        "cardinality_constraint_not_matched", "WARNING", fieldName, "should", "at least", cardinality
                ));
                return false;
            }
        }
        if (cardinalityType == CardinalityType.MAX && cardValue > cardinality) {
            if (isMandatory()) {
                throw new TabStructureError(
                        i18n.msg("cardinality_constraint_not_matched", "ERROR", fieldName, "must", "at most", cardinality
                        ));
            } else {
                log.warn(i18n.msg(
                        "cardinality_constraint_not_matched", "WARNING", fieldName, "should", "at most", cardinality
                ));
                return false;
            }
        }
        if (cardinalityType == CardinalityType.EXACT && cardValue != cardinality) {
            if (isMandatory()) {
                throw new TabStructureError(
                        i18n.msg("cardinality_constraint_not_matched", "ERROR", fieldName, "must", "exaclty", cardinality
                        ));
            } else {
                log.warn(i18n.msg(
                        "cardinality_constraint_not_matched", "WARNING", fieldName, "should", "exactly", cardinality
                ));
                return false;
            }
        } // if block

        return true;
    } // checkCardinality ()

}

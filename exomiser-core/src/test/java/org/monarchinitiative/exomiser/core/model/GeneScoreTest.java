/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2021 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.monarchinitiative.exomiser.core.model;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.exomiser.core.genome.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class GeneScoreTest {

    @Test
    public void testEmpty() {
        assertThat(GeneScore.empty(), equalTo(GeneScore.builder().build()));
    }

    @Test
    public void getGeneIdentifier() {
        GeneIdentifier testIdentifier = GeneIdentifier.builder().geneSymbol("GENE:1").geneId("HGNC:12345").build();

        GeneScore instance = GeneScore.builder().geneIdentifier(testIdentifier).build();
        assertThat(instance.getGeneIdentifier(), equalTo(testIdentifier));
    }

    @Test
    public void getModeOfInheritance() {
        GeneScore instance = GeneScore.builder()
                .modeOfInheritance(ModeOfInheritance.AUTOSOMAL_DOMINANT)
                .build();
        assertThat(instance.getModeOfInheritance(), equalTo(ModeOfInheritance.AUTOSOMAL_DOMINANT));
    }

    @Test
    public void getCombinedScore() {
        GeneScore instance = GeneScore.builder()
                .combinedScore(1d)
                .build();
        assertThat(instance.getCombinedScore(), equalTo(1d));
    }

    @Test
    public void getPhenotypeScore() {
        GeneScore instance = GeneScore.builder()
                .phenotypeScore(1d)
                .build();
        assertThat(instance.getPhenotypeScore(), equalTo(1d));
    }

    @Test
    public void getVariantScore() {
        GeneScore instance = GeneScore.builder()
                .variantScore(1d)
                .build();
        assertThat(instance.getVariantScore(), equalTo(1d));
    }

    @Test
    public void getContributingVariants() {
        List<VariantEvaluation> contributingVariants = List.of(
                TestFactory.variantBuilder(1, 12335, "T", "C").build(),
                TestFactory.variantBuilder(1, 23446, "A", "T").build()
        );

        GeneScore instance = GeneScore.builder()
                .contributingVariants(contributingVariants)
                .build();
        assertThat(instance.getContributingVariants(), equalTo(contributingVariants));
    }

    @Test
    public void equals() {
        assertThat(GeneScore.builder().build(), equalTo(GeneScore.builder().build()));
    }

    @Test
    public void testMaxCombinedScore() {
        GeneScore max = GeneScore.builder().combinedScore(1d).build();
        GeneScore other = GeneScore.builder().combinedScore(0.5).build();

        assertThat(GeneScore.max(max, other), equalTo(max));
        assertThat(GeneScore.max(other, max), equalTo(max));

        GeneScore positiveZero = GeneScore.builder().combinedScore(0).build();
        GeneScore negativeZero = GeneScore.builder().combinedScore(-0).build();

        assertThat(GeneScore.max(positiveZero, negativeZero), equalTo(positiveZero));
        assertThat(GeneScore.max(negativeZero, positiveZero), equalTo(positiveZero));
    }

    @Test
    public void testCompareTo() {
        GeneIdentifier first = GeneIdentifier.builder().geneSymbol("1ABC").build();
        GeneIdentifier second = GeneIdentifier.builder().geneSymbol("1BBC").build();
        GeneIdentifier third = GeneIdentifier.builder().geneSymbol("2BCD").build();

        // After an analysis, a single gene will have different scores due to different inheritance modes but these have
        // been omitted for clarity of what is actually being tested
        GeneScore one = GeneScore.builder().combinedScore(1d).geneIdentifier(first).build();
        GeneScore two = GeneScore.builder().combinedScore(0.5).geneIdentifier(first).build();
        GeneScore three = GeneScore.builder().combinedScore(0.5).geneIdentifier(second).build();
        GeneScore four = GeneScore.builder().combinedScore(0.25).geneIdentifier(second).build();
        GeneScore five = GeneScore.builder().combinedScore(0.25).geneIdentifier(third).build();
        GeneScore six = GeneScore.builder().combinedScore(0.125).geneIdentifier(third).build();

        List<GeneScore> geneScores = Arrays.asList(two, three, six, one, five, four);

        geneScores.sort(GeneScore::compareTo);

        assertThat(geneScores, equalTo(List.of(one, two, three, four, five, six)));
    }

    @Test
    public void testToString() {
        GeneScore instance = GeneScore.builder()
                .geneIdentifier(GeneIdentifier.builder().geneSymbol("TEST1").geneId("HGNC:12345").build())
                .combinedScore(1)
                .phenotypeScore(1)
                .variantScore(1)
                .modeOfInheritance(ModeOfInheritance.AUTOSOMAL_DOMINANT)
                .build();
        assertThat(instance.toString(), equalTo("GeneScore{geneIdentifier=GeneIdentifier{geneId='HGNC:12345', geneSymbol='TEST1', hgncId='', hgncSymbol='', entrezId='', ensemblId='', ucscId=''}, modeOfInheritance=AUTOSOMAL_DOMINANT, combinedScore=1.0, phenotypeScore=1.0, variantScore=1.0, contributingVariants=[]}"));
    }
}
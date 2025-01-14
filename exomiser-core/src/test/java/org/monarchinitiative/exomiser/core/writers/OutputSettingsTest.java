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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monarchinitiative.exomiser.core.writers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.exomiser.core.filters.FilterResult;
import org.monarchinitiative.exomiser.core.filters.FilterType;
import org.monarchinitiative.exomiser.core.model.Gene;
import org.monarchinitiative.exomiser.core.model.GeneScore;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class OutputSettingsTest {

    @Test
    public void testThatDefaultOutputPassVariantsOptionIsFalse() {
        OutputSettings instance = OutputSettings.builder().build();
        assertThat(instance.outputContributingVariantsOnly(), equalTo(false));
    }

    @Test
    public void testThatBuilderProducesOutputPassVariantsOptionWhenSet() {
        OutputSettings instance = OutputSettings.builder()
                .outputContributingVariantsOnly(true)
                .build();
        assertThat(instance.outputContributingVariantsOnly(), equalTo(true));
    }

    /**
     * Test of getNumberOfGenesToShow method, of class ExomiserSettings.
     */
    @Test
    public void testThatDefaultNumberOfGenesToShowIsZero() {
        OutputSettings instance = OutputSettings.builder().build();
        assertThat(instance.getNumberOfGenesToShow(), equalTo(0));
    }

    @Test
    public void testThatBuilderCanSetNumberOfGenesToShow() {
        int numGenes = 200;
        OutputSettings instance = OutputSettings.builder()
                .numberOfGenesToShow(numGenes)
                .build();
        assertThat(instance.getNumberOfGenesToShow(), equalTo(numGenes));
    }

    /**
     * Test of getOutputPrefix method, of class ExomiserSettings.
     */
    @Test
    public void testThatBuilderProducesDefaultOutFileName() {
        OutputSettings instance = OutputSettings.builder().build();
        assertThat(instance.getOutputPrefix(), equalTo(""));
    }

    @Test
    public void testThatBuilderProducesSetOutFileName() {
        String outputPrefix = "wibble";
        OutputSettings instance = OutputSettings.builder()
                .outputPrefix(outputPrefix)
                .build();
        assertThat(instance.getOutputPrefix(), equalTo(outputPrefix));
    }

    /**
     * Test of getOutputFormats method, of class ExomiserSettings.
     */
    @Test
    public void testThatDefaultOutputFormatIsHtml() {
        OutputSettings instance = OutputSettings.builder().build();
        assertThat(instance.getOutputFormats(), equalTo(EnumSet.of(OutputFormat.HTML)));
    }

    @Test
    public void testThatBuilderProducesSetOutputFormat() {
        Set<OutputFormat> outputFormats = EnumSet.of(OutputFormat.TSV_GENE);
        OutputSettings instance = OutputSettings.builder()
                .outputFormats(outputFormats)
                .build();
        assertThat(instance.getOutputFormats(), equalTo(outputFormats));
    }

    @Test
    public void testHashCode() {
        OutputSettings instance = OutputSettings.builder().build();
        OutputSettings other = OutputSettings.builder().build();
        assertThat(instance.hashCode(), equalTo(other.hashCode()));
    }

    @Test
    public void testEquals() {
        OutputSettings instance = OutputSettings.builder().build();
        OutputSettings other = OutputSettings.builder().build();
        assertThat(instance, equalTo(other));
    }

    @Test
    public void testToString() {
        OutputSettings instance = OutputSettings.builder().build();
        System.out.println(instance);
        assertThat(instance.toString().isEmpty(), is(false));
    }

    @Test
    public void testCanBuildFromYaml() throws Exception {
        OutputSettings instance = OutputSettings.builder().build();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        OutputSettings createdFromYaml = mapper.readValue(
                "outputContributingVariantsOnly: false\n"
                        + "numGenes: 0\n"
                        + "outputPrefix: \"\"\n"
                        + "outputFormats: [HTML]",
                OutputSettings.class);

        assertThat(instance, equalTo(createdFromYaml));
    }

    @Test
    public void testCanOutputAsYaml() throws Exception {
        OutputSettings instance = OutputSettings.builder().build();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String output = mapper.writeValueAsString(instance);
        String expected = "---\n" +
                "outputContributingVariantsOnly: false\n" +
                "minExomiserGeneScore: 0.0\n" +
                "outputPrefix: \"\"\n" +
                "outputFormats:\n" +
                "- \"HTML\"\n" +
                "numGenes: 0\n";
        assertThat(output, equalTo(expected));
    }

    @Test
    void testFilterGenes() {
        Gene overGeneCombinedScore1 = new Gene("GENE:1", 1);
        overGeneCombinedScore1.addFilterResult(FilterResult.pass(FilterType.INHERITANCE_FILTER));
        overGeneCombinedScore1.addGeneScore(GeneScore.builder().combinedScore(0.90f).build());

        Gene overGeneCombinedScore2 = new Gene("GENE:2", 2);
        overGeneCombinedScore2.addFilterResult(FilterResult.pass(FilterType.INHERITANCE_FILTER));
        overGeneCombinedScore2.addGeneScore(GeneScore.builder().combinedScore(0.80f).build());

        Gene overGeneCombinedScore3 = new Gene("GENE:3", 3);
        overGeneCombinedScore3.addFilterResult(FilterResult.pass(FilterType.INHERITANCE_FILTER));
        overGeneCombinedScore3.addGeneScore(GeneScore.builder().combinedScore(0.70f).build());

        Gene failedFilters = new Gene("GENE:4", 4);
        failedFilters.addFilterResult(FilterResult.fail(FilterType.INHERITANCE_FILTER));

        List<Gene> genes = List.of(overGeneCombinedScore1, overGeneCombinedScore2, overGeneCombinedScore3, failedFilters);

        OutputSettings highMinScoreAndNumGenes = OutputSettings.builder()
                .minExomiserGeneScore(0.9f)
                .numberOfGenesToShow(2)
                .build();
        assertThat(highMinScoreAndNumGenes.filterGenesForOutput(genes), equalTo(List.of(overGeneCombinedScore1)));

        OutputSettings minScoreAndNumGenes = OutputSettings.builder()
                .minExomiserGeneScore(0.7f)
                .numberOfGenesToShow(2)
                .build();
        assertThat(minScoreAndNumGenes.filterGenesForOutput(genes), equalTo(List.of(overGeneCombinedScore1, overGeneCombinedScore2)));

        OutputSettings minScoreAndAllGenes = OutputSettings.builder()
                .minExomiserGeneScore(0.7f)
                .numberOfGenesToShow(0)
                .build();
        assertThat(minScoreAndAllGenes.filterGenesForOutput(genes), equalTo(List.of(overGeneCombinedScore1, overGeneCombinedScore2, overGeneCombinedScore3)));

        assertThat(OutputSettings.defaults().filterGenesForOutput(genes), equalTo(List.of(overGeneCombinedScore1, overGeneCombinedScore2, overGeneCombinedScore3, failedFilters)));
        assertThat(OutputSettings.defaults().filterPassedGenesForOutput(genes), equalTo(List.of(overGeneCombinedScore1, overGeneCombinedScore2, overGeneCombinedScore3)));
    }
}

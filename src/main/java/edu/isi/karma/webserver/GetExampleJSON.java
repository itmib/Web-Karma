/*******************************************************************************
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/
package edu.isi.karma.webserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.isi.karma.controller.update.UpdateContainer;
import edu.isi.karma.controller.update.WorksheetHierarchicalDataUpdate;
import edu.isi.karma.controller.update.WorksheetHierarchicalHeadersUpdate;
import edu.isi.karma.controller.update.WorksheetListUpdate;
import edu.isi.karma.modeling.ontology.OntologyManager;
import edu.isi.karma.rep.Workspace;
import edu.isi.karma.rep.WorkspaceManager;
import edu.isi.karma.rep.metadata.Tag;
import edu.isi.karma.rep.metadata.TagsContainer.Color;
import edu.isi.karma.rep.metadata.TagsContainer.TagName;
import edu.isi.karma.view.VWorksheet;
import edu.isi.karma.view.VWorkspace;

public class GetExampleJSON extends HttpServlet {
	private enum Arguments {
		hasPreferenceId, workspacePreferencesId
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Workspace workspace = WorkspaceManager.getInstance().getFactory()
				.createWorkspace();

		/* Check and set the preferences key if required */
		VWorkspace vwsp = request.getParameter(Arguments.hasPreferenceId.name()).equals("true") ? 
			new VWorkspace(workspace, request.getParameter(Arguments.workspacePreferencesId.name())) : 
			new VWorkspace(workspace);

		WorkspaceRegistry.getInstance().register(new ExecutionController(vwsp));
		

		// Loading ontology to be preloaded
		OntologyManager mgr = workspace.getOntologyManager();
		mgr.doImport(new File("./Preloaded_Ontologies/geo_2007.owl"));
		
		//mariam
		/*
		File file = new File("../demofiles/peopleFaculty.csv");
		CSVFileImport imp = new CSVFileImport(1, 2, ',', '"', file, workspace.getFactory(), workspace);
		imp.generateWorksheet();
		
		//load ontologies
		OntologyManager om = workspace.getOntologyManager();
		//vivo ontology
		om.doImport(new File("../demofiles/vivo1.4-protege.owl"));
		//rdfs ontology
		om.doImport(new File("../demofiles/rdfs-small.owl"));
		*/
		//////////////

		// Initialize the Outlier tag
		Tag outlierTag = new Tag(TagName.Outlier, Color.Red);
		workspace.getTagsContainer().addTag(outlierTag);

		// SampleDataFactory.createSample1small(workspace);
//		SampleDataFactory.createSample1(workspace);
		// SampleDataFactory.createSampleJsonWithNestedTable2(false/* true: 2
		// rows */,
		// vwsp.getWorkspace());
		// //SampleDataFactory.createFlatWorksheet(workspace, 10000, 6);
		// SampleDataFactory.createFlatWorksheet(workspace, 2, 2);
		// //SampleDataFactory.createFromJsonTextFile(workspace,
		// "samplejson-1.txt");
		// SampleDataFactory.createJsonWithFunnyCharacters(workspace);
		// SampleDataFactory.createSampleJson(workspace, 3);
		// SampleDataFactory.createSampleJsonWithEmptyNestedTable1(workspace);
		// SampleDataFactory.createSampleJsonWithEmptyNestedTable2(workspace);
		// SampleDataFactory.createSampleJsonWithEmptyNestedTable3(workspace);
		// SampleDataFactory.createSampleJsonWithEmptyNestedTable4(workspace);
		// SampleDataFactory.createUnitTest1(workspace);
		// SampleDataFactory.createUnitTest2(workspace);
		// SampleDataFactory.createUnitTest3(workspace);
		// SampleDataFactory.createUnitTest4(workspace);
		// SampleDataFactory.createUnitTest5(workspace);
		// SampleDataFactory.createUnitTest6(workspace);
		// // SampleDataFactory.createFromJsonTextFile(workspace,
		// "unit-test-json.json");
		// // SampleDataFactory.createFromJsonTextFile(workspace,
		// "testUnitTest1.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "testUnitTest2.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "testUnitTest4.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "testUnitTest5.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "testUnitTest6.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "testSampleJsonWithEmptyNestedTable1.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "createSampleJsonWithNestedTable2.json");
		// SampleDataFactory.createFromJsonTextFile(workspace, "f6.json");
		// SampleDataFactory.createFromJsonTextFile(workspace,
		// "createSampleJsonWithNestedTable2_VD.json");
		// Put all created worksheet models in the view.
		vwsp.addAllWorksheets();

		UpdateContainer c = new UpdateContainer();
		c.add(new WorksheetListUpdate(vwsp.getVWorksheetList()));
		for (VWorksheet vw : vwsp.getVWorksheetList().getVWorksheets()) {
			c.add(new WorksheetHierarchicalHeadersUpdate(vw));
			c.add(new WorksheetHierarchicalDataUpdate(vw));
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		c.generateJson("", pw, vwsp);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(sw.toString());
	}
}

/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.classification;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNode;

// FIXME AT: this interface has been extracted automatically, must be reviewed.
public interface ClassificationActions extends Serializable {

    String EVENT_CLASSIFICATION_TREE_CHANGED = "classificationTreeChanged";

    String CLASSIFICATION_ROOTS_PROVIDER_NAME = "CLASSIFICATION_ROOTS";

    String CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER = "CURRENT_DOCUMENT_CLASSIFICATIONS";

    String CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION = "CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION";

    String CURRENT_SELECTION_FOR_UNCLASSIFICATION = "CURRENT_SELECTION_FOR_UNCLASSIFICATION";

    String BOOKMARKED_INTO = "bookmarked_into";

    String CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER = "CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER";

    String CURRENT_SELECTION_FOR_CLASSIFICATION = "CURRENT_SELECTION_FOR_CLASSIFICATION";

    String CURRENT_SELECTION_FOR_CLASSIFICATION_PAGE = "current_selection_classification_request";

    String CLASSIFICATION_DOCUMENTS_CONTENT_VIEW = "document_bookmark";

    String TREE_PLUGIN_NAME = "classification";

    boolean getCanClassifyFromCurrentSelection();

    /**
     * Returns target documents when classifying an envelope.
     * <p>
     * May take into account only current email, or all emails in current envelope.
     * </p>
     */
    Collection<DocumentModel> getTargetDocuments();

    /**
     * Returns selected target documents from a list of email documents.
     */
    Collection<DocumentModel> getMassTargetDocuments();

    /**
     * Classifies current email or envelope in given folder and redirect to current page.
     */
    String classify(ClassificationTreeNode node);

    /**
     * Classifies a list of emails in given folder and redirect to current page.
     */
    String massClassify(ClassificationTreeNode node);

    /**
     * Classifies given documents in given classification folder.
     *
     * @return true on error
     */
    boolean classify(Collection<DocumentModel> targetDocs, DocumentModel classificationFolder);

    String cancelClassification();

    String getCurrentClassificationRootId();

    /**
     * Sets current classification root id, and set it as current document.
     */
    void setCurrentClassificationRootId(String newRootId);

    String navigateToCurrentClassificationRoot();

    DocumentModel getCurrentClassificationRoot();

    String getCurrentEditableClassificationRootId();

    void setCurrentEditableClassificationRootId(String newRootId);

    DocumentModel getCurrentEditableClassificationRoot();

    DocumentTreeNode getCurrentClassificationTree();

    DocumentTreeNode getCurrentEditableClassificationTree();

    DocumentModelList getClassificationRoots();

    DocumentModelList getEditableClassificationRoots();

    void editableClassificationRootSelected(ValueChangeEvent event);

    void resetClassificationData();

    void resetCurrentDocumentClassifications();

    DocumentModelList getCurrentDocumentClassifications();

    /**
     * Returns classification form for selected documents
     *
     * @param currentViewId the current view id, so that redirection can be done correctly on cancel.
     */
    String showCurrentSelectionClassificationForm(String currentViewId);

    String cancelCurrentSelectionClassificationForm();

    void unclassify();

    /**
     * Unclassifies given document ids in given classification folder.
     *
     * @return true on error
     */
    boolean unclassify(Collection<String> targetDocIds, DocumentModel classificationFolder);

}

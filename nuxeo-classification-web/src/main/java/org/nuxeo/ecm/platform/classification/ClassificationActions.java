package org.nuxeo.ecm.platform.classification;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PagedDocumentsProvider;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.ui.web.api.ResultsProviderFarm;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.pagination.ResultsProviderFarmUserException;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNode;

// FIXME AT: this interface has been extracted automatically, must be reviewed.
public interface ClassificationActions extends ResultsProviderFarm,
        Serializable {

    public static final String EVENT_CLASSIFICATION_TREE_CHANGED = "classificationTreeChanged";

    public static final String CLASSIFICATION_ROOTS_PROVIDER_NAME = "CLASSIFICATION_ROOTS";

    public static final String CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER = "CURRENT_DOCUMENT_CLASSIFICATIONS";

    public static final String CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION = "CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION";

    public static final String CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER = "CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER";

    public static final String CURRENT_SELECTION_FOR_CLASSIFICATION = "CURRENT_SELECTION_FOR_CLASSIFICATION";

    public static final String CURRENT_SELECTION_FOR_CLASSIFICATION_PAGE = "current_selection_classification_request";

    public static final String TREE_PLUGIN_NAME = "classification";

    public boolean getCanClassifyFromCurrentSelection() throws ClientException;

    /**
     * Returns target documents when classifying an envelope.
     * <p>
     * May take into account only current email, or all emails in current
     * envelope.
     * </p>
     */
    public Collection<DocumentModel> getTargetDocuments()
            throws ClientException;

    /**
     * Returns selected target documents from a list of email documents.
     */
    Collection<DocumentModel> getMassTargetDocuments() throws ClientException;

    /**
     * Classifies current email or envelope in given folder and redirect to
     * current page.
     */
    public String classify(ClassificationTreeNode node) throws ClientException;

    /**
     * Classifies a list of emails in given folder and redirect to current page.
     */
    public String massClassify(ClassificationTreeNode node)
            throws ClientException;

    /**
     * Classifies given documents in given classification folder.
     *
     * @return true on error
     */
    @SuppressWarnings("unchecked")
    public boolean classify(Collection<DocumentModel> targetDocs,
            DocumentModel classificationFolder) throws ClientException;

    public String cancelClassification() throws ClientException;

    public String getCurrentClassificationRootId() throws ClientException;

    /**
     * Sets current classification root id, and set it as current document.
     */
    public void setCurrentClassificationRootId(String newRootId)
            throws ClientException;

    public String navigateToCurrentClassificationRoot() throws ClientException;

    public DocumentModel getCurrentClassificationRoot() throws ClientException;

    public String getCurrentEditableClassificationRootId()
            throws ClientException;

    public void setCurrentEditableClassificationRootId(String newRootId)
            throws ClientException;

    public DocumentModel getCurrentEditableClassificationRoot()
            throws ClientException;

    public DocumentTreeNode getCurrentClassificationTree()
            throws ClientException;

    public DocumentTreeNode getCurrentEditableClassificationTree()
            throws ClientException;

    public DocumentModelList getClassificationRoots() throws ClientException;

    public DocumentModelList getEditableClassificationRoots()
            throws ClientException;

    public void editableClassificationRootSelected(ValueChangeEvent event)
            throws ClientException;

    public void resetClassificationData();

    public void resetCurrentDocumentClassifications();

    public PagedDocumentsProvider getResultsProvider(String name,
            SortInfo sortInfo) throws ClientException,
            ResultsProviderFarmUserException;

    public PagedDocumentsProvider getResultsProvider(String name)
            throws ClientException, ResultsProviderFarmUserException;

    public DocumentModelList getCurrentDocumentClassifications()
            throws ClientException;

    public SelectDataModel getCurrentDocumentClassificationsSelection()
            throws ClientException;

    /**
     * Returns classification form for selected documents
     *
     * @param currentviewId the current view id, so that redirection can be done
     *            correctly on cancel.
     */
    public String showCurrentSelectionClassificationForm(String currentViewId)
            throws ClientException;

    public String cancelCurrentSelectionClassificationForm()
            throws ClientException;

    /**
     * Returns select data model for selected documents from previous documents
     * selection.
     */
    public SelectDataModel getCurrentSelectionEmailsSelection()
            throws ClientException;

    public void unclassify() throws ClientException;

    /**
     * Unclassifies given document ids in given classification folder.
     *
     * @return true on error
     */
    public boolean unclassify(Collection<String> targetDocIds,
            DocumentModel classificationFolder) throws ClientException;

}
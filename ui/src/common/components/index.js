import AddItems from './AddItems.vue';
import AddSpecimens from './AddSpecimens.vue';
import AddToCart from './AddToCart.vue';
import AuditOverview from './AuditOverview.vue';
import AuditRevisions from './AuditRevisions.vue';
import Avatar from './Avatar.vue';
import BooleanCheckbox from './BooleanCheckbox.vue';
import Breadcrumb from './Breadcrumb.vue';
import Button from './Button.vue';
import ButtonGroup from './ButtonGroup.vue';
import ButtonLink from './ButtonLink.vue';
import Chart from './Chart.vue';
import Checkbox from './Checkbox.vue';
import CloseSpecimen from './CloseSpecimen.vue';
import Col from './Col.vue';
import Confirm from './Confirm.vue';
import ConfirmDelete from './ConfirmDelete.vue';
import CopyLink from './CopyLink.vue';
import ContainerPositionSelector from './ContainerPositionSelector.vue';
import ContainerSelector from './ContainerSelector.vue';
import DatePicker from './DatePicker.vue';
import DeleteObject from './DeleteObject.vue';
import Dialog from './Dialog.vue';
import Divider from './Divider.vue';
import Dropdown from './Dropdown.vue';
import FileUpload from './FileUpload.vue';
import Form from './Form.vue';
import FormGroup from './FormGroup.vue';
import Grid from './Grid.vue';
import GridColumn from './GridColumn.vue';
import Icon from './Icon.vue';
import IconTitle from './IconTitle.vue';
import InlineMessage from './InlineMessage.vue';
import InputNumber from './InputNumber.vue';
import InputText from './InputText.vue';
import ItemsLabelScanner from './ItemsLabelScanner.vue';
import ItemsValidationDialog from './ItemsValidationDialog.vue';
import Label from './Label.vue';
import ListGroup from './ListGroup.vue';
import ListSize from './ListSize.vue';
import ListView from './ListView.vue';
import LoadingBar from './LoadingBar.vue';
import Mask from './Mask.vue';
import Menu from './Menu.vue';
import Message from './Message.vue';
import MultiProgressBar from './MultiProgressBar.vue';
import MultiSelectDropdown from './MultiSelectDropdown.vue';
import MultiSelectGroupDropdown from './MultiSelectGroupDropdown.vue';
import NewTab from './NewTab.vue';
import Note from './Note.vue';
import Overlay from './Overlay.vue';
import Overview from './Overview.vue';
import Page from './Page.vue';
import PageBody from './PageBody.vue';
import PageHeader from './PageHeader.vue';
import PageToolbar from './PageToolbar.vue';
import Pager from './Pager.vue';
import Panel from './Panel.vue';
import Password from './Password.vue';
import PluginViews from './PluginViews.vue';
import ProgressBar from './ProgressBar.vue';
import PvDropdown from './PvDropdown.vue';
import QueryListView from './QueryListView.vue';
import RadioButton from './RadioButton.vue';
import Screen from './Screen.vue';
import ScreenPanel from './ScreenPanel.vue';
import Section from './Section.vue';
import SideMenu from './SideMenu.vue';
import SignaturePad from './SignaturePad.vue';
import SiteDropdown from './SiteDropdown.vue';
import Span from './Span.vue';
import SpecimenActions from './SpecimenActions.vue';
import SpecimenDescription from './SpecimenDescription.vue';
import SpecimenMeasure from './SpecimenMeasure.vue';
import SpecimenStatusBall from './SpecimenStatusBall.vue';
import SpecimenType from './SpecimenType.vue';
import Star from './Star.vue';
import Steps from './Steps.vue';
import Step from './Step.vue';
import StorageContainerDropdown from './StorageContainerDropdown.vue';
import StoragePosition from './StoragePosition.vue';
import StoragePositionSelector from './StoragePositionSelector.vue';
import Subform from './Subform.vue';
import TabMenu from './TabMenu.vue';
import Table from './Table.vue';
import TableForm from './TableForm.vue';
import Tag from './Tag.vue';
import Textarea from './Textarea.vue';
import TabView from './TabView.vue';
import TabPanel from './TabPanel.vue';
import Task from './Task.vue';
import Unknown from './Unknown.vue';
import UserDropdown from './UserDropdown.vue';
import UsernameAvatar from './UsernameAvatar.vue';
import UtilisationBar from './UtilisationBar.vue';

export default {
  install(app) {
    app.component('os-add-items',        AddItems);
    app.component('os-add-specimens',    AddSpecimens);
    app.component('os-add-to-cart',      AddToCart);
    app.component('os-audit-overview',   AuditOverview);
    app.component('os-audit-revisions',  AuditRevisions);
    app.component('os-avatar',           Avatar);
    app.component('os-boolean-checkbox', BooleanCheckbox);
    app.component('os-breadcrumb',       Breadcrumb);
    app.component('os-button',           Button);
    app.component('os-button-group',     ButtonGroup);
    app.component('os-button-link',      ButtonLink);
    app.component('os-chart',            Chart);
    app.component('os-checkbox',         Checkbox);
    app.component('os-close-specimen',   CloseSpecimen);
    app.component('os-column',           Col);
    app.component('os-confirm',          Confirm);
    app.component('os-container-selector', ContainerSelector);
    app.component('os-container-position-selector', ContainerPositionSelector);
    app.component('os-confirm-delete',   ConfirmDelete);
    app.component('os-copy-link',        CopyLink);
    app.component('os-date-picker',      DatePicker);
    app.component('os-delete-object',    DeleteObject);
    app.component('os-dialog',           Dialog);
    app.component('os-divider',          Divider);
    app.component('os-dropdown',         Dropdown);
    app.component('os-file-upload',      FileUpload);
    app.component('os-form',             Form);
    app.component('os-form-group',       FormGroup);
    app.component('os-grid',             Grid);
    app.component('os-grid-column',      GridColumn);
    app.component('os-icon',             Icon);
    app.component('os-icon-title',       IconTitle);
    app.component('os-inline-message',   InlineMessage);
    app.component('os-input-number',     InputNumber);
    app.component('os-input-text',       InputText);
    app.component('os-item-labels-scanner', ItemsLabelScanner);
    app.component('os-items-validation', ItemsValidationDialog);
    app.component('os-label',            Label);
    app.component('os-list-group',       ListGroup);
    app.component('os-list-size',        ListSize);
    app.component('os-list-view',        ListView);
    app.component('os-loading-bar',      LoadingBar);
    app.component('os-mask',             Mask);
    app.component('os-menu',             Menu);
    app.component('os-message',          Message);
    app.component('os-multi-progress-bar', MultiProgressBar);
    app.component('os-multi-select-dropdown', MultiSelectDropdown);
    app.component('os-multi-select-group-dropdown', MultiSelectGroupDropdown);
    app.component('os-new-tab',          NewTab);
    app.component('os-note',             Note);
    app.component('os-overlay',          Overlay);
    app.component('os-overview',         Overview);
    app.component('os-page',             Page);
    app.component('os-page-body',        PageBody);
    app.component('os-page-head',        PageHeader);
    app.component('os-page-toolbar',     PageToolbar);
    app.component('os-pager',            Pager);
    app.component('os-panel',            Panel);
    app.component('os-password',         Password);
    app.component('os-plugin-views',     PluginViews);
    app.component('os-progress-bar',     ProgressBar);
    app.component('os-pv-dropdown',      PvDropdown);
    app.component('os-radio-button',     RadioButton);
    app.component('os-query-list-view',  QueryListView);
    app.component('os-screen',           Screen);
    app.component('os-screen-panel',     ScreenPanel);
    app.component('os-section',          Section);
    app.component('os-side-menu',        SideMenu);
    app.component('os-signature-pad',    SignaturePad);
    app.component('os-site-dropdown',    SiteDropdown);
    app.component('os-span',             Span);
    app.component('os-specimen-actions', SpecimenActions);
    app.component('os-specimen-description', SpecimenDescription);
    app.component('os-specimen-measure', SpecimenMeasure);
    app.component('os-specimen-status-icon', SpecimenStatusBall);
    app.component('os-specimen-type',    SpecimenType);
    app.component('os-containers-dropdown', StorageContainerDropdown);
    app.component('os-storage-position', StoragePosition);
    app.component('os-storage-position-selector', StoragePositionSelector);
    app.component('os-subform',          Subform);
    app.component('os-tab-menu',         TabMenu);
    app.component('os-table-span',       Table);
    app.component('os-table-form',       TableForm);
    app.component('os-tag',              Tag);
    app.component('os-textarea',         Textarea);
    app.component('os-tabs',             TabView);
    app.component('os-tab',              TabPanel);
    app.component('os-task',             Task);
    app.component('os-unknown',          Unknown);
    app.component('os-username-avatar',  UsernameAvatar);
    app.component('os-user-dropdown',    UserDropdown);
    app.component('os-star',             Star);
    app.component('os-steps',            Steps);
    app.component('os-step',             Step);
    app.component('os-utilisation-bar',  UtilisationBar);
  }
}

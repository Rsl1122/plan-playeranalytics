import ErrorView from "../ErrorView";
import LoadIn from "../../components/animation/LoadIn";
import {Card, Col, InputGroup, Row} from "react-bootstrap";
import React, {useState} from "react";
import CardHeader from "../../components/cards/CardHeader";
import {faFloppyDisk, faPlus, faRotateLeft, faTrash, faUserGroup, faUsersGear} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon as Fa, FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {GroupEditContextProvider, useGroupEditContext} from "../../hooks/context/groupEditContextHook";
import {fetchGroups} from "../../service/manageService";
import {useDataRequest} from "../../hooks/dataFetchHook";
import {CardLoader} from "../../components/navigation/Loader";
import {useTranslation} from "react-i18next";
import {
    ConfigurationStorageContextProvider,
    useConfigurationStorageContext
} from "../../hooks/context/configurationStorageContextHook";
import SideNavTabs from "../../components/layout/SideNavTabs";
import Select from "../../components/input/Select";
import Scrollable from "../../components/Scrollable";
import OpaqueText from "../../components/layout/OpaqueText";

const GroupsHeader = ({groupName, icon}) => {
    return (
        <tr>
            <td><FontAwesomeIcon icon={icon || faUserGroup}/> {groupName}</td>
        </tr>
    )
}

const PermissionDropdown = ({permission, checked, indeterminate, togglePermission, children, childNodes, root}) => {
    const {t} = useTranslation();

    const translationKey = "html.manage.permission.description." + permission?.split('.').join("_");
    const translated = t(translationKey);

    if (childNodes.length) {
        if (permission === undefined) {
            return <>{children}</>;
        } else {
            return (
                <details open style={root ? {marginLeft: "0.5rem"} : {marginLeft: "2.1rem"}}>
                    <summary>
                        <input className={"form-check-input"} type={"checkbox"} value={indeterminate ? "" : checked}
                               checked={checked}
                               ref={input => {
                                   if (input) input.indeterminate = indeterminate
                               }}
                               onChange={() => togglePermission(permission)}
                        /> {permission} {permission && translated !== translationKey &&
                        <OpaqueText inline>&middot; {translated}</OpaqueText>}
                        <hr style={{margin: 0}}/>
                    </summary>

                    {children}
                </details>
            )
        }
    } else {
        return (
            <li style={root ? {marginLeft: "1.4rem"} : {marginLeft: "3rem"}}>
                <input className={"form-check-input"} type={"checkbox"} value={checked}
                       checked={checked}
                       onChange={() => togglePermission(permission)}
                /> {permission} {permission && translated !== translationKey &&
                <OpaqueText inline>&middot; {translated}</OpaqueText>}
            </li>
        )
    }
}

const PermissionTree = ({nodes, isIndeterminate, isChecked, togglePermission}) => {
    if (!nodes.length) {
        return <></>;
    }
    return (
        <>
            {nodes.map(node => <>
                <PermissionDropdown permission={node.permission} root={node.parentIndex === -1}
                                    indeterminate={isIndeterminate(node.permission)}
                                    checked={isChecked(node.permission)}
                                    togglePermission={togglePermission}
                                    childNodes={node.children}>
                    <PermissionTree nodes={node.children}
                                    togglePermission={togglePermission}
                                    isChecked={isChecked}
                                    isIndeterminate={isIndeterminate}/>
                </PermissionDropdown>
            </>)}
        </>
    )
}

const GroupsBody = ({groups}) => {
    const {
        changed,
        groupName,
        permissionTree,
        togglePermission,
        isChecked,
        isIndeterminate
    } = useGroupEditContext();

    return (
        <Row>
            <Col>
                <div>
                    <h3 style={{display: "inline-block"}}>Permissions of {groupName}</h3>
                    <UnsavedChangesText visible={changed}/>
                    <DeleteGroupButton groupName={groupName} groups={groups}/>
                </div>
                <Scrollable>
                    <PermissionTree nodes={[permissionTree]}
                                    childNodes={permissionTree.children}
                                    togglePermission={togglePermission}
                                    isChecked={isChecked}
                                    isIndeterminate={isIndeterminate}/>
                </Scrollable>
            </Col>
        </Row>
    );
}

const SaveButton = () => {
    const {dirty, requestSave} = useConfigurationStorageContext();

    return (
        <button className={"float-end btn bg-theme"} style={{margin: "-0.5rem"}}
                disabled={!dirty}
                onClick={requestSave}>
            <Fa icon={faFloppyDisk}/> Save
        </button>
    )
}

const DeleteGroupButton = ({groupName, groups}) => {
    const [clicked, setClicked] = useState(false);
    const [moveToGroup, setMoveToGroup] = useState(0);

    if (clicked) {
        const groupOptions = groups.filter(g => g.name !== groupName).map(g => g.name);
        return (
            <Card>
                <CardHeader icon={faTrash} label={`Delete ${groupName}`}/>
                <Card.Body>
                    <InputGroup>
                        <div className={"input-group-text"}>
                            Move remaining users to group
                        </div>
                        <Select options={groupOptions}
                                selectedIndex={moveToGroup} setSelectedIndex={setMoveToGroup}/>
                    </InputGroup>

                    <p className={"mt-3"}>This will move all users of '{groupName}' to group
                        '{groupOptions[moveToGroup]}'. There is no undo!</p>

                    <button className={"btn bg-red mt-2"}
                            onClick={() => {
                                setClicked(true)
                            }}>
                        <Fa icon={faTrash}/> Confirm & delete {groupName}
                    </button>
                    <button className={"btn bg-grey-outline mt-2"} style={{marginLeft: "0.5rem"}}
                            onClick={() => {
                                setClicked(false)
                            }}>
                        <Fa icon={faRotateLeft}/> Cancel
                    </button>
                </Card.Body>
            </Card>
        )
    }

    return (
        <button className={"float-end btn bg-grey-outline"}
                onClick={() => {
                    setClicked(true)
                }}>
            <Fa icon={faTrash}/> Delete {groupName}
        </button>
    )
}

const DiscardButton = () => {
    const {dirty, requestDiscard} = useConfigurationStorageContext();

    return (
        <>
            {dirty && <button className={"float-end btn"} style={{margin: "-0.5rem", marginRight: "0.5rem"}}
                              onClick={requestDiscard}>
                <Fa icon={faTrash}/> Discard Changes
            </button>}
        </>
    )
}

const UnsavedChangesText = ({visible}) => {
    const {dirty} = useConfigurationStorageContext();
    const show = visible !== undefined ? visible : dirty;
    if (show) {
        return (
            <p style={{display: "inline-block", marginLeft: "1rem", marginBottom: 0, opacity: 0.6}}>Unsaved changes</p>
        )
    } else {
        return <></>
    }
}

const AddGroupBody = ({groups}) => {
    const [invalid, setInvalid] = useState(false);
    const [value, setValue] = useState(undefined);

    const onChange = (event) => {
        const newValue = event.target.value;
        setValue(newValue.toLowerCase().replace(" ", "_"));
        setInvalid(newValue.length > 100);
    }

    return (
        <Card>
            <CardHeader icon={faPlus} label={"Add group"}/>
            <Card.Body>
                <InputGroup>
                    <div className={"input-group-text"}>
                        <FontAwesomeIcon icon={faUserGroup}/>
                    </div>
                    <input type="text" className={"form-control" + (invalid ? " is-invalid" : '')}
                           placeholder={"Name of the group"}
                           value={value}
                           onChange={onChange}
                    />
                    {invalid && <div className="invalid-feedback">
                        Group name can be 100 characters maximum.
                    </div>}
                </InputGroup>
                <button className={"btn bg-plan mt-2"} disabled={invalid || !value || value.length === 0}
                        onClick={() => {
                        }}>
                    <Fa icon={faFloppyDisk}/> Add group
                </button>
            </Card.Body>
        </Card>
    )
}

const GroupsCard = ({groups}) => {
    const slices = groups.map(group => {
        return {
            body: <GroupEditContextProvider groupName={group.name}><GroupsBody
                groups={groups}/></GroupEditContextProvider>,
            header: <GroupsHeader groupName={group.name}/>,
            color: 'light-green',
            outline: false
        }
    })

    slices.push({
        body: <AddGroupBody groups={groups}/>,
        header: <GroupsHeader groupName={"Add group"} icon={faPlus}/>,
        color: 'light-green',
        outline: false
    })

    return (
        <ConfigurationStorageContextProvider>
            <Card>
                <CardHeader icon={faUsersGear} color="theme" label={"Manage Group Permissions"}>
                    <UnsavedChangesText/>
                    <SaveButton/>
                    <DiscardButton/>
                </CardHeader>
                <Card.Body>
                    <SideNavTabs slices={slices} open></SideNavTabs>
                </Card.Body>
            </Card>
        </ConfigurationStorageContextProvider>
    )
}

const GroupsView = () => {
    const {data, loadingError} = useDataRequest(fetchGroups, [null]);

    if (loadingError) return <ErrorView error={loadingError}/>
    if (!data) return <CardLoader/>

    return (
        <LoadIn>
            <Row>
                <Col md={12}>
                    <GroupsCard groups={data.groups}/>
                </Col>
            </Row>
        </LoadIn>
    )
};

export default GroupsView
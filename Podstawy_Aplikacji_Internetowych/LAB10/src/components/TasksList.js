import React from 'react'

function TasksList(props) {

    const list2 = props.list.map(task => (
        props.hideCompleted && task.checked ? null :
            <li style={{ textDecoration: task.checked ? 'line-through' : null }}>
                <input
                    type="checkbox"
                    checked={task.checked}
                    onChange={event => {
                        props.onItemChange(task.id);
                    }}
                />
                {task.text}
            </li>
    ));

    return (
        <div>
            {
                (props.list.length === 0 || (props.hideCompleted && props.list.every(el => el.checked)) ?
                "Empty List":
                <ul>
                    {list2}
                </ul>)
            }
        </div>
    )
}

export default TasksList;
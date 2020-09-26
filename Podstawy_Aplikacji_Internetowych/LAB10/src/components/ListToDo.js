import React, { Component } from 'react'
import TasksList from './TasksList.js';

class ListToDo extends Component {
    constructor() {
        super();

        this.state = {
            list: [],
            textBox: '',
            counter: 0,
            hideCompleted: false
        }
    }

    updateTextBox = event => {
        this.setState({
            textBox: event.target.value
        });
    }

    onClickButton = event => {
        this.setState(prevState => {
            let task = {
                id: prevState.counter + 1,
                text: prevState.textBox,
                checked: false
            };
            if(prevState.textBox !== "")
            {
                return {
                    list: [...prevState.list, task],
                    textBox: "",
                    counter: prevState.counter + 1
                };
            }
            else{
                return {}
            }
            
        });
    }

    onItemChange = id =>
    {
        this.setState(prevState=>
            {
                const newList = prevState.list.map( task => {
                    if(task.id === id){
                        task.checked = !task.checked;
                        return task;
                    }
                    else return task;
                });
                return{
                    list:newList
                };
            });
    }

    onToggleHide = event => {
        this.setState(prev => {
            return {
                hideCompleted : !prev.hideCompleted
            }
        });
    }

    render() {

        return (
            <div>
                <h1>List to do</h1>
                <hr />
                <input type='checkbox' onChange={this.onToggleHide}/>
                <label>  hide completed</label>
                <hr />
                <TasksList
                    list={this.state.list}
                    hideCompleted={this.state.hideCompleted}
                    onItemChange={this.onItemChange}
                />
                <hr/>
                <input type={Text}
                    onChange={this.updateTextBox}
                    value={this.state.textBox}
                />
                <button onClick={this.onClickButton}>Add</button>

            </div>
        )
    }
}
export default ListToDo;
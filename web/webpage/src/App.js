import './App.css'
import React, {useState} from "react";
import axios from 'axios'
import qs from 'qs'
import { Button, List, Input, Form } from 'antd';

class App extends React.Component {
    constructor() {
        super();
        this.state = {
            userList:[],
            selectedUserId: 0,
        }
    }
    formRef = React.createRef();
    async componentDidMount() {
        this.refresh()
    }
    refresh(){
        axios.get('http://localhost:8080/api/users') .then((res)=>(
            this.setState({userList : [...res.data]})
        ));
    }
    onAddUser = (values) => {
        this.addUser()
    }
    addUser(){
        let data={
            name: this.formRef.current.getFieldValue('name'),
            email: this.formRef.current.getFieldValue('email'),
            telephone:this.formRef.current.getFieldValue('telephone'),
            job: this.formRef.current.getFieldValue('job'),
            age: this.formRef.current.getFieldValue('age'),
        }
        axios.post('http://localhost:8080/api/addUser',qs.stringify(data),{headers:{'Content-Type':'application/x-www-form-urlencoded'}})
            .then(res=>{
                this.refresh()
                this.resetInputFields()
            })
    }
    editUser(user){
        this.formRef.current.setFieldsValue({
            name: user.name,
            email: user.email,
            telephone: user.telephone,
            job: user.job,
            age: user.age,
        });
        this.state.selectedUserId=user.uid
    }
    deleteUser(user){
        this.state.selectedUserId=user.uid
        let data={
            uid: this.state.selectedUserId
        }
        axios.post('http://localhost:8080/api/deleteUser',qs.stringify(data),{headers:{'Content-Type':'application/x-www-form-urlencoded'}})
            .then(res=>{
                this.refresh()
            })
    }
    onFinish = (values) => {
        let data={
            uid: this.state.selectedUserId,
            name: this.formRef.current.getFieldValue('name'),
            email: this.formRef.current.getFieldValue('email'),
            telephone:this.formRef.current.getFieldValue('telephone'),
            job: this.formRef.current.getFieldValue('job'),
            age: this.formRef.current.getFieldValue('age'),
        }
        axios.post('http://localhost:8080/api/editUser',qs.stringify(data),{headers:{'Content-Type':'application/x-www-form-urlencoded'}})
        .then(res=>{
            this.refresh()
            this.resetInputFields()
        })
    };
    onReset = () => {
        this.resetInputFields()
    };
    resetInputFields(){
        this.formRef.current.resetFields();
    }
    render() {

        return (
            <>
                <List
                    bordered
                    dataSource={this.state.userList}
                    renderItem={item => (
                        <List.Item key={item.id}>
                            <List.Item.Meta
                                title={item.name}
                                description={item.email}
                            />
                            <Button type="primary" onClick = {() => {
                                this.editUser(item);
                            }}>编辑</Button>
                            <Button type="primary" danger onClick={() => {
                                this.deleteUser(item);
                            }}>删除</Button>

                        </List.Item>
                    )}
                />
                <Form
                    ref={this.formRef}
                    name="basic"
                    labelCol={{ span: 8 }}
                    wrapperCol={{ span: 10 }}
                    initialValues={{ name: "" }}
                    onFinish={this.onAddUser}
                    autoComplete="off"
                >
                    <Form.Item
                        label="姓名"
                        name="name"
                        rules={[{ required: true, message: '请输入姓名' }]}
                    >
                        <Input placeholder={'请输入姓名'}/>
                    </Form.Item>

                    <Form.Item
                        label="邮箱"
                        name="email"
                        rules={[{ required: true, message: '请输入邮箱' }]}
                    >
                        <Input placeholder={'请输入邮箱'}/>
                    </Form.Item>
                    <Form.Item
                        label="电话"
                        name="telephone"
                        rules={[{ required: true, message: '请输入电话' }]}
                    >
                        <Input placeholder={'请输入电话'}/>
                    </Form.Item>
                    <Form.Item
                        label="职务"
                        name="job"
                        rules={[{ required: true, message: '请输入职务' }]}
                    >
                        <Input placeholder={'请输入电话'}/>
                    </Form.Item>
                    <Form.Item
                        label="年龄"
                        name="age"
                        rules={[{ required: true, message: '请输入年龄' }]}
                    >
                        <Input placeholder={'请输入年龄'}/>
                    </Form.Item>
                    <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                        <Button type="primary" htmlType="submit">
                            提交
                        </Button>
                        <Button onClick={this.onReset}>重置</Button>
                    </Form.Item>
                </Form>
            </>
        );
    }
}

export default App;
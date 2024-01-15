import React , {Component} from 'react';

export default class PageTest extends Component{

    id = 2
    
    state = {
      information: [
        {
          id: 0,
          name: '김민준',
          phone: '010-0000-0000'
        },
        {
          id: 1,
          name: '홍길동',
          phone: '010-0000-0001'
        }
      ]
    }

    handleChange = (e) =>{
        this.setState({
            [e.target.name] : e.target.value
        });
    }
    handleCreate = (data) => {
        const { information } = this.state;
        this.setState({
          information: information.concat({ id: this.id++, ...data })
        })
      }
    render(){
        const { information } = this.state;
        return(
            <form onFocus={this.handleCreate}>
                <input placeholder="이름" value={this.state.name} onChange={this.handleChange} name="name" />
                <input placeholder="전화번호" value={this.state.phone} onChange={this.handleChange} name="phone" />
        <div>{JSON.stringify(information)}</div>
            </form>
        );
    }
}


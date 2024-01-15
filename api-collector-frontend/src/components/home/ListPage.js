import React,{Component} from 'react';
import DefaultCard from './Card';
import './ListPage.css';
import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from '@material-ui/core/styles';
import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import Grid from '@material-ui/core/Grid';

export default class ListPage extends Component {
    id = 1;
    state={
      card:[],
      cols: 1
    }

    render(){
      const {card,cols} = this.props;
      console.log(card);
      return(
            <div className="gridList">
              {card &&
                card.map((rowdata, rowIndex) => {
                  return (
                    <Grid key={rowIndex} className="gridDiv">
                      <Grid container item xs={parseInt(cols)} spacing={3}>
                        {rowdata && rowdata.map((itemdata,colIndex) => {
                          return (
                            <DefaultCard
                              key={colIndex}
                              id={itemdata.id}
                              Align="Vertical"
                              ImageURL={itemdata.ImageURL}
                              Title={itemdata.Title}
                              SubTitle={itemdata.SubTitle}
                              SubDesc={itemdata.SubDesc}
                              FirstBtn={itemdata.FirstBtn}
                              SecondBtn={itemdata.SecondBtn}
                              Value="0.8"
                            />
                          );
                        })};
                      </Grid>
                    </Grid>
                  );
                })}
          </div>
        );
    }
}

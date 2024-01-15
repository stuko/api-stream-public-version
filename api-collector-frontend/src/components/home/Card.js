import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import GaugeChart from 'react-gauge-chart'

const useStyles = makeStyles((theme) => ({
    root: {
        flexGrow: 1,
        flexDirection: 'column',
        textAlign: 'center',
        alignItems: 'center',
    },
    paper: {
        padding: theme.spacing(1),
        // color: theme.palette.text.secondary,
        flexDirection: 'column',
        width: '100%',
        textAlign: 'center',
        alignItems: 'center',
        color: '#000000',
    },
    column: {
        marginBottom: 5,
        textAlign: 'center',
        alignItems: 'center',

    },
    img: {
        marginBottom: 5,
        width: 200,
        textAlign: 'center',
        alignItems: 'center',
        color: '#000000',
        background: '#FFFFFF',

    },
    title: {
        marginBottom: 5,
        width: 100,
        textAlign: 'center',
        alignItems: 'center',

    },
    subTitle: {
        marginBottom: 5,
        width: 100,
        textAlign: 'center',
        alignItems: 'center',

    },
    subDesc: {
        marginBottom: 5,
        width: 100,
        textAlign: 'center',
        alignItems: 'center',

    },
    button: {
        marginBottom: 5,
        width: 100,
        textAlign: 'center',
        alignItems: 'center',

    }
}));

 export default function DefaultCard({ id, Align, ImageURL, Title, SubTitle,SubDesc,FirstBtn,SecondBtn, Value }) {
 
  const classes = useStyles();

  let style,row,paperWidth;

  if(Align == 'Vertical'){
     style = { padding: 50 ,
              flexDirection: 'column',
              textAlign: 'center',
              alignItems : 'center',
              marginBottom : 10,};
     row=3;
     paperWidth = {width:400};
  }else{
      style = { padding: 50 ,
                 flexDirection: 'row',
                 textAlign: 'right',
                 alignItems : 'left',
                 marginBottom : 10,
              }
      row=1;
      paperWidth = {width:'85vw'};
  }

  let chart;

  if(true){
    if(Align == 'Vertical') chart = <div className={classes.img}>
            <GaugeChart id={'chart-'+id}
                nrOfLevels={420}
                arcsLength={[0.3, 0.5, 0.2]}
                colors={['#5BE12C', '#F5CD19', '#EA4228']}
                textColor={'#000000'}
                percent={parseFloat(Value)}
                arcPadding={0.02}
            />
            </div>;
    else chart = <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>
              <GaugeChart id={'chart-'+id}
                  nrOfLevels={420}
                  arcsLength={[0.3, 0.5, 0.2]}
                  colors={['#5BE12C', '#F5CD19', '#EA4228']}
                  textColor={'#000000'}
                  percent={parseFloat(Value)}
                  arcPadding={0.02}
              />
              </div>;            
  }else{
    chart = null;
  }

  let title,subTitle;
  if (true) {
    if(Align == 'Vertical') title = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>{Title}</div>;
    else title = <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>{Title}</div>;
  }else{
    title = null;
  }

  if (true) {
    if(Align == 'Vertical') subTitle = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>{SubTitle}<p>{SubDesc}</p></div>;
    else subTitle = <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>{SubTitle}<p>{SubDesc}</p></div>;
  }else{
    subTitle = null;
  }

  let firstButton,secondButton;

  if (true) {
    if(Align == 'Vertical') firstButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}> 
                   <Button
                    type="button"
                    variant="contained"
                    color="primary"
                    className={classes.submit}
                    // onClick={this.login}
                    >{FirstBtn}</Button>
                  </div>;
    else firstButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}><Button
                        type="button"
                        variant="contained"
                        color="primary"
                        className={classes.submit + " float-right"}
                        // onClick={this.login}
                        >{FirstBtn}</Button>
                      </div>;                  
  } else {
    firstButton = null;
  }

  if (true) {
    if(Align == 'Vertical') secondButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>
                    <Button 
                      type="button"
                      variant="contained"
                      color="primary"
                      className={classes.submit}
                      // onClick={this.login}
                      >{SecondBtn}</Button>
                   </div>;
    else secondButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}><Button 
                        type="button"
                        variant="contained"
                        color="primary"
                        className={classes.submit + " float-right"}
                        // onClick={this.login}
                        >{SecondBtn}</Button></div>;                   
  } else {
    secondButton = null;
  }

  return (
    <div style={style}>
        <Grid key={id} item xs={12} className={classes.root}>
        <Paper className={classes.paper} style={paperWidth}>
          <Grid container spacing={row}>
          <Grid item xs>
           {chart}
          </Grid>
          <Grid item xs>
           {title}
          </Grid> 
          <Grid item xs>
           {subTitle}
          </Grid>
          <Grid item xs>
           {firstButton}
           {secondButton}
          </Grid>   
          </Grid>                  
        </Paper>
        </Grid>
    </div>
  );
}


import React, {Component} from 'react';
import axios from "axios"; 
import ReactPlayer from 'react-player'
import Box from '@material-ui/core/Box';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import SkipPreviousIcon from '@material-ui/icons/SkipPrevious';
import PlayArrowIcon from '@material-ui/icons/PlayArrow';
import SkipNextIcon from '@material-ui/icons/SkipNext';

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
    margin : 20,
    maxWidth: 645
  },
  title: {
    marginBottom : 50,
    color: '#03045e',
    fontWeight:'bold'
  },
  details: {
    display: 'flex',
    flexDirection: 'column',
    backgroundColor: '#ade8f4',
    color: '#03045e',
    padding : 20

  },
  content: {
    flex: '1 0 auto',
    color: '#03045e',
    width: 245,
    padding: 10
  },
  cover: {
    width: 151,
  },
  controls: {
    display: 'flex',
    alignItems: 'center',
    paddingLeft: theme.spacing(1),
    paddingBottom: theme.spacing(1),
  },
  playIcon: {
    height: 38,
    width: 38,
  },
}));

export default function Home() {

  const classes = useStyles();
  const theme = useTheme();
  return(
    <div style={{padding: 100}}>

      <h2 className={classes.title}>"처음 사용하시는 분들은 아래 동영상을 참고해 주세요"</h2>

      <table>
        <tr>
        <td>
          <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                API 정보를 등록 하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                외부 OPEN API 사이트에 방문하셔서, URL 정보와 인자값 정보를 확인하신후,
                등록해 주세요.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-api.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>
        </td>
        <td>
        <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                API 인증키 정보를 등록 하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                외부 OPEN API 사이트에 방문하셔서, 가입 후 제공받은 인증키 정보를 등록하는 방법입니다.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-cert.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>          
        </td>
        </tr>
        <tr>
        <td>
        <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                배치 스케줄 정보를 등록 하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                실제 실행 하고 자 하는 배치의 스케줄을 등록하는 방법입니다.<br/>
                스케줄은 crontab에 등록하시는 방법과 동일하게 사용하시면 됩니다.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-schedule.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>          
        </td>
        <td>
        <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                배치의 토폴로지(프로세스 연관관계)를 등록하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                스트림 기반으로 작동하는 배치 토폴로지를 직접 그려서 배치를 개발 하는 방법을 보여 줍니다.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-topology.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>          
        </td>
        </tr>
        <tr>
        <td>
        <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                배치 토폴로지를 반영하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                배치 토폴로지를 개발하거나 수정하신 경우, 실제 배치 프로세스에 반영하기 위한 방법입니다.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-run.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>          
        </td>
        <td>
        <Card className={classes.root}>
          <div className={classes.details}>
            <CardContent className={classes.content}>
              <Typography component="h5" variant="h5">
                배치 실행 결과(데이터)를 확인 하는 방법
              </Typography>
              <Typography  variant="body2" color="textSecondary" component="p" className={classes.details}>
                각 배치들의 실행 결과가 저장 되어 있는 MongoDB에 데이터를 확인 하는 방법입니다.
              </Typography>
            </CardContent>
            
          </div>
          <ReactPlayer 
          style={{ border: '1px solid black',borderRadius: '5px!important' }}
          className='react-player'
          url='/mov/sample-log.mp4' 
          playing={false} 
          controls={true}
          />
          </Card>          
        </td>
        </tr>
      </table>
    </div>
  );
}
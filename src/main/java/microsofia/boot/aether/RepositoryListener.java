package microsofia.boot.aether;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * RepositoryListener that prints on the console.
 * */
public class RepositoryListener extends AbstractRepositoryListener{
	private PrintStream out;
	private Date date;
	private SimpleDateFormat simpleDateFormat;

	public RepositoryListener(){
		this( null );
	}

	public RepositoryListener( PrintStream out ){
		this.out = ( out != null ) ? out : System.out;
		date=new Date();
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	protected synchronized StringBuffer getDate(){
		StringBuffer sb=new StringBuffer();
		date.setTime(System.currentTimeMillis());
		sb.append(simpleDateFormat.format(date));
		return sb;
	}
	
	public void artifactDeployed( RepositoryEvent event ){
		out.println(getDate()+" Deployed " + event.getArtifact() + " to " + event.getRepository() );
	}

	public void artifactDeploying( RepositoryEvent event ){
		out.println(getDate()+" Deploying " + event.getArtifact() + " to " + event.getRepository() );
	}

	public void artifactDescriptorInvalid( RepositoryEvent event ){
		out.println(getDate()+" Invalid artifact descriptor for " + event.getArtifact() + ": "+ event.getException().getMessage() );
	}

	public void artifactDescriptorMissing( RepositoryEvent event ){
		out.println(getDate()+" Missing artifact descriptor for " + event.getArtifact() );
	}

    public void artifactInstalled( RepositoryEvent event ){
        out.println(getDate()+" Installed " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactInstalling( RepositoryEvent event ){
        out.println(getDate()+" Installing " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactResolved( RepositoryEvent event ){
        out.println(getDate()+" Resolved artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactDownloading( RepositoryEvent event ){
        out.println(getDate()+" Downloading artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactDownloaded( RepositoryEvent event ){
        out.println(getDate()+" Downloaded artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactResolving( RepositoryEvent event ){
        out.println(getDate()+" Resolving artifact " + event.getArtifact() );
    }

    public void metadataDeployed( RepositoryEvent event ){
        out.println(getDate()+" Deployed " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataDeploying( RepositoryEvent event ){
        out.println(getDate()+" Deploying " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataInstalled( RepositoryEvent event ){
        out.println(getDate()+" Installed " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInstalling( RepositoryEvent event ){
        out.println(getDate()+" Installing " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInvalid( RepositoryEvent event ){
        out.println(getDate()+" Invalid metadata " + event.getMetadata() );
    }

    public void metadataResolved( RepositoryEvent event ){
        out.println(getDate()+" Resolved metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

    public void metadataResolving( RepositoryEvent event ){
        out.println(getDate()+" Resolving metadata " + event.getMetadata() + " from " + event.getRepository() );
    }
}

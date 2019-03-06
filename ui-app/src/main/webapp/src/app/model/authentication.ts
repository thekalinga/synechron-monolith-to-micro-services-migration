import { User } from './user';

export class Authentication {
  refreshToken: string;
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  scope?: string;
  user: User;

  receivedAt: number;

  public static newInstance(
    obj: any,
    fromApiResponse: boolean
  ): Authentication {
    let authentication: Authentication = null;
    if (obj !== null) {
      authentication = new Authentication();
      if (fromApiResponse) {
        authentication.refreshToken = obj.refresh_token;
        authentication.accessToken = obj.access_token;
        authentication.tokenType = obj.token_type;
        authentication.expiresIn = obj.expires_in;
      } else {
        authentication.refreshToken = obj.refreshToken;
        authentication.accessToken = obj.accessToken;
        authentication.tokenType = obj.tokenType;
        authentication.expiresIn = obj.expiresIn;
      }
      authentication.scope = obj.scope;
      authentication.user = obj.user as User;
      authentication.receivedAt = new Date().getTime();
    }
    return authentication;
  }

  public looksValid(): boolean {
    return (
      this.receivedAt &&
      this.expiresIn &&
      new Date().getTime() <= this.receivedAt + this.expiresIn * 1000
    );
  }
}

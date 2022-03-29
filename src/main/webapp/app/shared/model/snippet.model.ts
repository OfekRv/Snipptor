import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';

export interface ISnippet {
  id?: number;
  hash?: string | null;
  content?: string;
  url?: string | null;
  snippetMatchedRules?: ISnippetMatchedRules[] | null;
}

export const defaultValue: Readonly<ISnippet> = {};

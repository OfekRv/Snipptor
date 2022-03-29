import { IRule } from 'app/shared/model/rule.model';
import { ISnippet } from 'app/shared/model/snippet.model';

export interface ISnippetMatchedRules {
  id?: number;
  rules?: IRule[] | null;
  snippets?: ISnippet[] | null;
}

export const defaultValue: Readonly<ISnippetMatchedRules> = {};

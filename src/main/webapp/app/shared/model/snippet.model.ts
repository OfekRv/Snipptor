import { ISnippetMatchedRules } from 'app/shared/model/snippet-matched-rules.model';
import { SnippetClassification } from 'app/shared/model/enumerations/snippet-classification.model';

export interface ISnippet {
  id?: number;
  hash?: string | null;
  content?: string;
  url?: string | null;
  classification?: SnippetClassification | null;
  scanCount?: number | null;
  matchedRules?: ISnippetMatchedRules | null;
}

export const defaultValue: Readonly<ISnippet> = {};
